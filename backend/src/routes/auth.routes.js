import { Router } from 'express';
import { login, logout, refresh, register } from '../controllers/auth.controller.js';
import { validate } from '../middleware/validate.js';
import {
  loginValidation,
  refreshTokenValidation,
  registerValidation,
} from '../validations/auth.validation.js';

const router = Router();

/**
 * @route   POST /api/v1/auth/register
 * @desc    Register a new customer account
 * @access  Public
 */
router.post('/register', validate(registerValidation), register);

/**
 * @route   POST /api/v1/auth/login
 * @desc    Authenticate user with email & password, returns access/refresh tokens
 * @access  Public
 */
router.post('/login', validate(loginValidation), login);

/**
 * @route   POST /api/v1/auth/refresh
 * @desc    Rotate and refresh access token using a valid refresh token
 * @access  Public
 */
router.post('/refresh', validate(refreshTokenValidation), refresh);

/**
 * @route   POST /api/v1/auth/logout
 * @desc    Revoke the active session / refresh token from Redis
 * @access  Public / Authenticated
 */
router.post('/logout', logout);

export default router;
