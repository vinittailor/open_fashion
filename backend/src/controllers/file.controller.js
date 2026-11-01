import * as fileService from '../services/file.service.js';
import { ApiResponse } from '../utils/apiResponse.js';

/**
 * Handles single file upload.
 * POST /api/v1/files/upload
 */
export const uploadSingleFile = async (req, res, next) => {
  try {
    const file = await fileService.createFileRecord(
      req,
      req.file,
      req.user?.id || null
    );

    return ApiResponse.created(res, 'File uploaded successfully', { file });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles batch multi-file upload with optional product gallery linking.
 * POST /api/v1/files/upload-multiple
 */
export const uploadMultipleFiles = async (req, res, next) => {
  try {
    const result = await fileService.createMultipleFileRecords(
      req,
      req.files,
      req.user?.id || null,
      req.body.productId || null
    );

    return ApiResponse.created(res, 'Files uploaded successfully', result);
  } catch (error) {
    next(error);
  }
};

/**
 * Handles linking existing file IDs to a product gallery.
 * POST /api/v1/files/link-product
 */
export const linkFilesToProductGallery = async (req, res, next) => {
  try {
    const { productId, images } = req.body;
    const productImages = await fileService.linkFilesToProduct(productId, images);
    return ApiResponse.created(res, 'Product images linked successfully', { productImages });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles fetching file metadata by ID.
 * GET /api/v1/files/:id
 */
export const getFileDetails = async (req, res, next) => {
  try {
    const file = await fileService.getFileById(req.params.id);
    return ApiResponse.success(res, 'File retrieved successfully', { file });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles file deletion from disk and database.
 * DELETE /api/v1/files/:id
 */
export const deleteFile = async (req, res, next) => {
  try {
    const file = await fileService.deleteFile(req.params.id, req.user);
    return ApiResponse.success(res, 'File deleted successfully', { file });
  } catch (error) {
    next(error);
  }
};
