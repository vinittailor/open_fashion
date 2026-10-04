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
