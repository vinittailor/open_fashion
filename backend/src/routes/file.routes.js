import { Router } from 'express';
import {
  uploadSingleFile,
  getFileDetails,
  deleteFile,
} from '../controllers/file.controller.js';
import { authenticate } from '../middleware/auth.js';
import { uploadSingle } from '../middleware/upload.js';

const router = Router();

/**
 * @route   POST /api/v1/files/upload
 * @desc    Upload a single image file (multipart/form-data with field 'file')
 * @access  Private (Authenticated Users)
 */
router.post('/upload', authenticate, uploadSingle('file'), uploadSingleFile);

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
