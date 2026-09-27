import { Router } from 'express';
import { register } from '../controllers/auth.controller.js';
import { validate } from '../middleware/validate.js';
import { registerValidation } from '../validations/auth.validation.js';

const router = Router();

/**
 * @route   POST /api/v1/auth/register
 * @desc    Register a new customer account
 * @access  Public
 */
router.post('/register', validate(registerValidation), register);

export default router;
