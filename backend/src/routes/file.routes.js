import { Router } from 'express';
import {
  uploadSingleFile,
  uploadMultipleFiles,
  linkFilesToProductGallery,
  getFileDetails,
  deleteFile,
} from '../controllers/file.controller.js';
import { authenticate } from '../middleware/auth.js';
import { uploadSingle, uploadMultiple } from '../middleware/upload.js';

const router = Router();

/**
 * @route   POST /api/v1/files/upload
 * @desc    Upload a single image file (multipart/form-data with field 'file')
 * @access  Private (Authenticated Users)
 */
router.post('/upload', authenticate, uploadSingle('file'), uploadSingleFile);

/**
 * @route   POST /api/v1/files/upload-multiple
 * @desc    Upload multiple image files (multipart/form-data with field 'files', max 10)
 *          Optionally associates with productId provided in form-data
 * @access  Private (Authenticated Users)
 */
router.post(
  '/upload-multiple',
  authenticate,
  uploadMultiple('files', 10),
  uploadMultipleFiles
);

/**
 * @route   POST /api/v1/files/link-product
 * @desc    Link existing uploaded file IDs to a ProductImage gallery
 * @access  Private (Authenticated Users)
 */
router.post('/link-product', authenticate, linkFilesToProductGallery);

/**
 * @route   GET /api/v1/files/:id
 * @desc    Retrieve file metadata by UUID
 * @access  Public
 */
router.get('/:id', getFileDetails);

/**
 * @route   DELETE /api/v1/files/:id
 * @desc    Delete a file (Owner or ADMIN only)
 * @access  Private
 */
router.delete('/:id', authenticate, deleteFile);

export default router;
