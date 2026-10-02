import { Router } from "express";
import {
  login,
  logout,
  refresh,
  register,
  forgotPassword,
  resetPassword,
  sendVerification,
  verifyEmail,
} from "../controllers/auth.controller.js";
import { validate } from "../middleware/validate.js";
import { authenticate } from "../middleware/auth.js";
import {
  loginValidation,
  refreshTokenValidation,
  registerValidation,
  forgotPasswordValidation,
  resetPasswordValidation,
  verifyEmailValidation,
} from "../validations/auth.validation.js";

const router = Router();

/**
 * @route   POST /api/v1/auth/register
 * @desc    Register a new customer account
 * @access  Public
 */
router.post("/register", validate(registerValidation), register);

/**
 * @route   POST /api/v1/auth/login
 * @desc    Authenticate user with email & password, returns access/refresh tokens
 * @access  Public
 */
router.post("/login", validate(loginValidation), login);

/**
 * @route   POST /api/v1/auth/refresh
 * @desc    Rotate and refresh access token using a valid refresh token
 * @access  Public
 */
router.post("/refresh", validate(refreshTokenValidation), refresh);

/**
 * @route   POST /api/v1/auth/logout
 * @desc    Revoke the active session / refresh token from Redis
 * @access  Public / Authenticated
 */
router.post("/logout", logout);

/**
 * @route   POST /api/v1/auth/forgot-password
 * @desc    Request a password reset link and 6-digit OTP
 * @access  Public
 */
router.post(
  "/forgot-password",
  validate(forgotPasswordValidation),
  forgotPassword,
);

/**
 * @route   POST /api/v1/auth/reset-password
 * @desc    Submit token/OTP and new password to complete password reset
 * @access  Public
 */
router.post(
  "/reset-password",
  validate(resetPasswordValidation),
  resetPassword,
);

/**
 * @route   POST /api/v1/auth/send-verification
 * @desc    Send a new email verification token & OTP to the logged-in user
 * @access  Protected (Requires Bearer JWT)
 */
router.post("/send-verification", authenticate, sendVerification);

/**
 * @route   POST /api/v1/auth/verify-email
 * @desc    Verify email address using token or OTP
 * @access  Public
 */
router.post("/verify-email", validate(verifyEmailValidation), verifyEmail);

export default router;
