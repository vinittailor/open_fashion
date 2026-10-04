import multer from 'multer';
import path from 'node:path';
import fs from 'node:fs';
import crypto from 'node:crypto';
import { BadRequestError } from './errorHandler.js';

// Base root directory for all uploaded files
const UPLOADS_ROOT = path.resolve('uploads');

// Helper: Creates a folder synchronously if it doesn't already exist
const ensureDirectoryExists = (dirPath) => {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true });
  }
};


// Ensure default upload folders exist on server boot
ensureDirectoryExists(UPLOADS_ROOT);
ensureDirectoryExists(path.join(UPLOADS_ROOT, 'avatars'));
ensureDirectoryExists(path.join(UPLOADS_ROOT, 'products'));
ensureDirectoryExists(path.join(UPLOADS_ROOT, 'general'));

// 1. Configure Multer Disk Storage Engine
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    // Dynamically categorize subfolder based on field name or URL route
    let subfolder = 'general';
    if (file.fieldname === 'avatar' || req.originalUrl.includes('/avatar')) {
      subfolder = 'avatars';
    } else if (file.fieldname === 'product' || req.originalUrl.includes('/products')) {
      subfolder = 'products';
    }

    const targetDir = path.join(UPLOADS_ROOT, subfolder);
    ensureDirectoryExists(targetDir);
    cb(null, targetDir);
  },

  filename: (req, file, cb) => {
    // Generate collision-safe filename: img-<timestamp>-<randomHex><ext>
    const randomHash = crypto.randomBytes(8).toString('hex');
    const timestamp = Date.now();
    const ext = path.extname(file.originalname).toLowerCase() || '.jpg';

    // Sanitize extension to only alphanumeric characters and dot
    const cleanExt = ext.replace(/[^a-z0-9.]/gi, '');
    const safeFilename = `img-${timestamp}-${randomHash}${cleanExt}`;

    cb(null, safeFilename);
  },
});

// 2. MIME Type Security Whitelist Filter
const ALLOWED_MIME_TYPES = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif',
  'image/avif',
  'image/heic',
  'image/heif',
]);

const fileFilter = (req, file, cb) => {
  if (ALLOWED_MIME_TYPES.has(file.mimetype.toLowerCase())) {
    // Accept file -> proceed to save
    cb(null, true);
  } else {
    // Reject file -> stop immediately with a 400 Bad Request error
    cb(
      new BadRequestError(
        `Invalid file format (${file.mimetype}). Allowed types: JPEG, PNG, WEBP, GIF, AVIF, HEIC`
      ),
      false
    );
  }
};

// 3. Create Configured Multer Instance with 5MB Limit
export const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: 5 * 1024 * 1024, // 5 Megabytes maximum per file
    files: 10,                 // Maximum 10 files per request
  },
});

/**
 * Middleware for single file upload (populates `req.file`)
 * @param {string} fieldName Form-data field name (default: 'file')
 */
export const uploadSingle = (fieldName = 'file') => upload.single(fieldName);

/**
 * Middleware for multiple file uploads (populates `req.files` array)
 * @param {string} fieldName Form-data field name (default: 'files')
 * @param {number} maxCount Maximum allowed files in batch (default: 5)
 */
export const uploadMultiple = (fieldName = 'files', maxCount = 5) =>
  upload.array(fieldName, maxCount);
