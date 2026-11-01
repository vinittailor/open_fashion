import path from 'node:path';
import fs from 'node:fs/promises';
import { prisma } from '../config/prisma.js';
import { env } from '../config/env.js';
import { NotFoundError, BadRequestError, ForbiddenError } from '../middleware/errorHandler.js';

/**
 * Builds the fully qualified, publicly accessible URL for a stored file.
 * In development: "http://127.0.0.1:5000/uploads/avatars/img-123.webp"
 * In production: "https://cdn.openfashion.com/avatars/img-123.webp"
 *
 * @param {import('express').Request} req
 * @param {string} relativeKey (e.g. "avatars/img-123.webp")
 * @returns {string} Fully qualified public URL
 */
export const buildPublicFileUrl = (req, relativeKey) => {
  // Normalize Windows backslashes (\) to standard forward slashes (/)
  const normalizedKey = relativeKey.replace(/\\/g, '/');

  // If a CDN or specific asset domain is configured in .env, use it
  if (env.CDN_BASE_URL) {
    return `${env.CDN_BASE_URL.replace(/\/$/, '')}/${normalizedKey}`;
  }

  // Otherwise, construct dynamic URL from incoming Express request
  const protocol = req.protocol || 'http';
  const host = req.get('host') || '127.0.0.1:5000';
  return `${protocol}://${host}/uploads/${normalizedKey}`;
};

/**
 * Registers an uploaded file in the PostgreSQL File model registry.
 *
 * @param {import('express').Request} req - Express request
 * @param {Express.Multer.File} fileData - Multer file object from req.file
 * @param {string} [uploaderId] - UUID of the authenticated user
 * @returns {Promise<Object>} Created File registry record
 */
export const createFileRecord = async (req, fileData, uploaderId = null) => {
  if (!fileData) {
    throw new BadRequestError('No file was provided for upload');
  }

  // Extract relative subfolder name (e.g. "avatars", "products", or "general")
  const uploadsRoot = path.resolve('uploads');
  const relativeSubfolder = path.relative(uploadsRoot, fileData.destination) || '';
  
  // Construct relative key: "avatars/img-172804-abc.webp"
  const relativeKey = path.join(relativeSubfolder, fileData.filename).replace(/\\/g, '/');
  
  // Construct accessible URL
  const publicUrl = buildPublicFileUrl(req, relativeKey);

  // Insert into PostgreSQL database via Prisma
  const fileRecord = await prisma.file.create({
    data: {
      filename: fileData.filename,
      key: relativeKey,
      url: publicUrl,
      mimeType: fileData.mimetype,
      sizeBytes: fileData.size,
      provider: 'LOCAL',
      isPublic: true,
      uploaderId: uploaderId || null,
    },
  });

  return fileRecord;
};

/**
 * Registers multiple uploaded files in the PostgreSQL File model registry in a single batch,
 * optionally linking them to a Product as ProductImage records.
 *
 * @param {import('express').Request} req - Express request
 * @param {Express.Multer.File[]} filesData - Array of Multer file objects from req.files
 * @param {string} [uploaderId] - UUID of the authenticated user
 * @param {string} [productId] - Optional UUID of product to associate images with
 * @returns {Promise<{ files: Array, productImages?: Array }>}
 */
export const createMultipleFileRecords = async (req, filesData, uploaderId = null, productId = null) => {
  if (!filesData || !Array.isArray(filesData) || filesData.length === 0) {
    throw new BadRequestError('No files were provided for upload');
  }

  const uploadsRoot = path.resolve('uploads');

  // Verify product exists if productId is provided
  if (productId) {
    const product = await prisma.product.findUnique({
      where: { id: productId },
    });
    if (!product) {
      throw new NotFoundError(`Product with ID ${productId} not found`);
    }
  }

  // Pre-calculate metadata and URLs for all uploaded files
  const filePayloads = filesData.map((fileData) => {
    const relativeSubfolder = path.relative(uploadsRoot, fileData.destination) || '';
    const relativeKey = path.join(relativeSubfolder, fileData.filename).replace(/\\/g, '/');
    const publicUrl = buildPublicFileUrl(req, relativeKey);

    return {
      filename: fileData.filename,
      key: relativeKey,
      url: publicUrl,
      mimeType: fileData.mimetype,
      sizeBytes: fileData.size,
      provider: 'LOCAL',
      isPublic: true,
      uploaderId: uploaderId || null,
    };
  });

  // Execute in an interactive transaction to guarantee consistency
  return await prisma.$transaction(async (tx) => {
    const createdFiles = [];
    for (const payload of filePayloads) {
      const record = await tx.file.create({ data: payload });
      createdFiles.push(record);
    }

    let createdProductImages = [];
    if (productId) {
      const existingImagesCount = await tx.productImage.count({
        where: { productId },
      });

      for (let i = 0; i < createdFiles.length; i++) {
        const file = createdFiles[i];
        const isPrimary = existingImagesCount === 0 && i === 0;
        const pImg = await tx.productImage.create({
          data: {
            productId,
            fileId: file.id,
            url: file.url,
            isPrimary,
            sortOrder: existingImagesCount + i,
          },
        });
        createdProductImages.push(pImg);
      }
    }

    return {
      files: createdFiles,
      ...(productId ? { productImages: createdProductImages } : {}),
    };
  });
};

/**
 * Links an array of existing file IDs to a product gallery.
 *
 * @param {string} productId - Product UUID
 * @param {Array<{ fileId: string, isPrimary?: boolean, sortOrder?: number }>} imageLinks
 * @returns {Promise<Array>} Created or updated ProductImage records
 */
export const linkFilesToProduct = async (productId, imageLinks) => {
  const product = await prisma.product.findUnique({
    where: { id: productId },
  });
  if (!product) {
    throw new NotFoundError(`Product with ID ${productId} not found`);
  }

  return await prisma.$transaction(async (tx) => {
    const results = [];
    for (const link of imageLinks) {
      const file = await tx.file.findUnique({
        where: { id: link.fileId },
      });
      if (!file) {
        throw new NotFoundError(`File with ID ${link.fileId} not found`);
      }

      if (link.isPrimary) {
        // Demote previous primary images for this product
        await tx.productImage.updateMany({
          where: { productId, isPrimary: true },
          data: { isPrimary: false },
        });
      }

      const pImg = await tx.productImage.create({
        data: {
          productId,
          fileId: file.id,
          url: file.url,
          isPrimary: link.isPrimary || false,
          sortOrder: link.sortOrder || 0,
        },
      });
      results.push(pImg);
    }
    return results;
  });
};


/**
 * Retrieves a file metadata record by ID.
 *
 * @param {string} fileId - UUID of the file
 * @returns {Promise<Object>} File entity
 */
export const getFileById = async (fileId) => {
  const file = await prisma.file.findUnique({
    where: { id: fileId },
    include: {
      uploader: {
        select: {
          id: true,
          name: true,
          email: true,
        },
      },
    },
  });

  if (!file) {
    throw new NotFoundError(`File with ID ${fileId} not found`);
  }

  return file;
};

/**
 * Deletes a file both physically from disk and logically from the PostgreSQL database.
 * Only the original uploader or an ADMIN is permitted to delete the file.
 *
 * @param {string} fileId - UUID of the file to delete
 * @param {Object} user - Authenticated user payload { id, role }
 * @returns {Promise<Object>} Deleted file metadata
 */
export const deleteFile = async (fileId, user) => {
  const file = await prisma.file.findUnique({
    where: { id: fileId },
  });

  if (!file) {
    throw new NotFoundError(`File with ID ${fileId} not found`);
  }

  // Security Check: Only the file uploader OR an ADMIN can delete the file
  const isOwner = file.uploaderId === user.id;
  const isAdmin = user.role === 'ADMIN';

  if (!isOwner && !isAdmin) {
    throw new ForbiddenError('You do not have permission to delete this file');
  }

  // 1. Delete physical file from local disk (if provider is LOCAL)
  if (file.provider === 'LOCAL') {
    const absoluteDiskPath = path.resolve('uploads', file.key);
    try {
      await fs.unlink(absoluteDiskPath);
    } catch {
      // If file is already gone from disk, proceed with DB deletion
    }
  }

  // 2. Delete database record in PostgreSQL
  const deletedFile = await prisma.file.delete({
    where: { id: fileId },
  });

  return deletedFile;
};
