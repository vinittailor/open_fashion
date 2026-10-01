import { Router } from 'express';
import { getMe, updateMe } from '../controllers/user.controller.js';
import { authenticate } from '../middleware/auth.js';
import { validate } from '../middleware/validate.js';
import { updateProfileValidation } from '../validations/user.validation.js';

const router = Router();

// All /users routes require authentication
router.use(authenticate);

/**
 * @route   GET /api/v1/users/me
 * @desc    Get authenticated user's profile
 * @access  Private
 */
router.get('/me', getMe);

/**
 * @route   PATCH /api/v1/users/me
 * @desc    Update authenticated user's profile
 * @access  Private
 */
router.patch('/me', validate(updateProfileValidation), updateMe);

export default router;
