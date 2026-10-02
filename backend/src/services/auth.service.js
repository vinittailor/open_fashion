import { prisma } from '../config/prisma.js';
import { redis } from '../config/redis.js';
import { hashPassword, comparePassword } from '../utils/password.js';
import { generateAuthTokens, verifyRefreshToken } from '../utils/jwt.js';
import {
  generateRandomHex,
  generateNumericOtp,
  hashToken,
} from '../utils/crypto.utils.js';
import { logger } from '../utils/logger.js';
import {
  storeRefreshToken,
  validateStoredRefreshToken,
  revokeRefreshToken,
} from './session.service.js';
import {
  ConflictError,
  UnauthorizedError,
  BadRequestError,
} from '../middleware/errorHandler.js';

/**
 * Standard projection for safe user data export (excluding passwordHash).
 */
const USER_PROJECTION = {
  id: true,
  name: true,
  email: true,
  role: true,
  phoneNumber: true,
  isEmailVerified: true,
  createdAt: true,
  updatedAt: true,
};

/**
 * Registers a new customer account in the database.
 */
export const registerUser = async ({ name, email, password, phoneNumber }) => {
  const existingUser = await prisma.user.findUnique({
    where: { email },
    select: { id: true },
  });

  if (existingUser) {
    throw new ConflictError('An account with this email address already exists.');
  }

  const passwordHash = await hashPassword(password);

  const newUser = await prisma.user.create({
    data: {
      name,
      email,
      passwordHash,
      phoneNumber: phoneNumber || null,
      role: 'CUSTOMER',
    },
    select: USER_PROJECTION,
  });

  return newUser;
};

/**
 * Authenticates a user with email and password, returning tokens and saving the session in Redis.
 *
 * @param {Object} credentials
 * @param {string} credentials.email
 * @param {string} credentials.password
 * @returns {Promise<{ user: Object, tokens: Object }>}
 */
export const loginUser = async ({ email, password }) => {
  // 1. Fetch user by email including passwordHash for comparison
  const user = await prisma.user.findFirst({
    where: {
      email,
      deletedAt: null, // Exclude soft-deleted accounts
    },
  });

  // Generic unauthorized error to prevent account enumeration
  if (!user) {
    throw new UnauthorizedError('Invalid email or password.');
  }

  // 2. Constant-time Bcrypt password comparison
  const isPasswordMatch = await comparePassword(password, user.passwordHash);

  if (!isPasswordMatch) {
    throw new UnauthorizedError('Invalid email or password.');
  }

  // 3. Generate dual token bundle
  const tokens = generateAuthTokens(user);

  // 4. Store refresh token in Redis whitelist
  await storeRefreshToken(user.id, tokens.refreshToken);

  // 5. Build sanitized user object
  const { passwordHash, ...sanitizedUser } = user;

  return {
    user: sanitizedUser,
    tokens,
  };
};

/**
 * Exchanges a valid Refresh Token for a new token pair (Refresh Token Rotation).
 *
 * @param {string} refreshToken - Active Refresh Token.
 * @returns {Promise<{ user: Object, tokens: Object }>}
 */
export const refreshAccessToken = async (refreshToken) => {
  // 1. Cryptographically verify JWT signature & expiration
  let decoded;
  try {
    decoded = verifyRefreshToken(refreshToken);
  } catch (error) {
    throw new UnauthorizedError('Invalid or expired refresh token.');
  }

  // 2. Validate against Redis whitelist (ensures token wasn't revoked)
  const isWhitelisted = await validateStoredRefreshToken(decoded.id, refreshToken);

  if (!isWhitelisted) {
    throw new UnauthorizedError('Session has expired or was revoked. Please sign in again.');
  }

  // 3. Fetch active user
  const user = await prisma.user.findFirst({
    where: {
      id: decoded.id,
      deletedAt: null,
    },
    select: USER_PROJECTION,
  });

  if (!user) {
    throw new UnauthorizedError('User account not found or disabled.');
  }

  // 4. Issue new rotated tokens & update Redis whitelist
  const newTokens = generateAuthTokens(user);
  await storeRefreshToken(user.id, newTokens.refreshToken);

  return {
    user,
    tokens: newTokens,
  };
};

/**
 * Terminates user session by deleting the active refresh token from Redis.
 *
 * @param {string} userId - User's UUID.
 * @returns {Promise<void>}
 */
export const logoutUser = async (userId) => {
  await revokeRefreshToken(userId);
};


/**
 * Initiates a password reset flow:
 * Generates a secure hex token & 6-digit OTP, stores their SHA-256 hashes in Redis (15m TTL),
 * and logs the reset instructions.
 *
 * @param {string} email - User's registered email
 * @returns {Promise<{ message: string, devToken?: string, devOtp?: string }>}
 */
export const requestPasswordReset = async (email) => {
  const user = await prisma.user.findFirst({
    where: {
      email,
      deletedAt: null,
    },
  });

  // Generic response to prevent account enumeration attacks
  if (!user) {
    logger.warn(`Password reset requested for non-existent email: ${email}`);
    return {
      message: 'If an account with that email exists, password reset instructions have been sent.',
    };
  }

  // 1. Generate 64-char Hex Token and 6-digit OTP
  const rawHexToken = generateRandomHex(32);
  const rawOtp = generateNumericOtp();

  // 2. Hash both with SHA-256 before caching
  const tokenHash = hashToken(rawHexToken);
  const otpHash = hashToken(rawOtp);

  // 3. Store in Redis with 15 minutes (900 seconds) expiration
  const RESET_TTL_SECONDS = 15 * 60; // 900s
  await redis.set(
    `pwd_reset:token:${tokenHash}`,
    JSON.stringify({ userId: user.id, email: user.email }),
    'EX',
    RESET_TTL_SECONDS
  );
  await redis.set(
    `pwd_reset:otp:${user.email}`,
    JSON.stringify({ otpHash, userId: user.id }),
    'EX',
    RESET_TTL_SECONDS
  );

  logger.info(`🔑 [DEV ONLY] Password Reset Token: ${rawHexToken}`);
  logger.info(`🔢 [DEV ONLY] Password Reset OTP for ${email}: ${rawOtp}`);

  return {
    message: 'If an account with that email exists, password reset instructions have been sent.',
    devToken: rawHexToken,
    devOtp: rawOtp,
  };
};
/**
 * Verifies the reset token or OTP, updates the user's password in PostgreSQL,
 * revokes all existing refresh tokens in Redis, and cleans up reset keys.
 *
 * @param {Object} params
 * @param {string} params.token - Raw 64-char hex token or 6-digit OTP
 * @param {string} params.newPassword - Validated new plain password
 * @param {string} [params.email] - Optional email for OTP direct lookup
 * @returns {Promise<{ message: string }>}
 */
export const resetPassword = async ({ token, newPassword, email }) => {
  const inputHash = hashToken(token);
  let userId = null;
  let userEmail = email;

  // 1. Check if token matches a 64-character URL Hex Token
  const tokenRecordJson = await redis.get(`pwd_reset:token:${inputHash}`);

  if (tokenRecordJson) {
    const data = JSON.parse(tokenRecordJson);
    userId = data.userId;
    userEmail = data.email;
  } else if (email) {
    // 2. Check if token matches a 6-digit OTP for this email
    const otpRecordJson = await redis.get(`pwd_reset:otp:${email}`);
    if (otpRecordJson) {
      const data = JSON.parse(otpRecordJson);
      if (data.otpHash === inputHash) {
        userId = data.userId;
      }
    }
  }

  if (!userId) {
    throw new BadRequestError('Invalid or expired password reset token.');
  }

  // 3. Hash the new password using Bcrypt
  const newPasswordHash = await hashPassword(newPassword);

  // 4. Update password in PostgreSQL
  await prisma.user.update({
    where: { id: userId },
    data: { passwordHash: newPasswordHash },
  });

  // 5. Security: Revoke all existing sessions on other devices
  await revokeRefreshToken(userId);

  // 6. Clean up Redis reset keys (Prevent reuse / replay attacks)
  await redis.del(`pwd_reset:token:${inputHash}`);
  if (userEmail) {
    await redis.del(`pwd_reset:otp:${userEmail}`);
  }

  logger.info(`✅ Password successfully reset for user ID: ${userId}`);

  return {
    message: 'Password has been reset successfully. Please log in with your new password.',
  };
};

/**
 * Generates and stores an email verification token (24h TTL) and numeric OTP (15m TTL) in Redis.
 *
 * @param {string} userId - User's UUID
 * @returns {Promise<{ message: string, devToken?: string, devOtp?: string }>}
 */
export const sendEmailVerification = async (userId) => {
  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: { id: true, email: true, isEmailVerified: true },
  });

  if (!user) {
    throw new BadRequestError('User not found.');
  }

  if (user.isEmailVerified) {
    throw new BadRequestError('Email address is already verified.');
  }

  // 1. Generate 64-char Hex Token (for URL links) and 6-digit OTP (for mobile app)
  const rawHexToken = generateRandomHex(32);
  const rawOtp = generateNumericOtp();

  const tokenHash = hashToken(rawHexToken);
  const otpHash = hashToken(rawOtp);

  // 2. Store in Redis: 24h for web links, 15m for mobile OTP
  const VERIFY_TOKEN_TTL_SECONDS = 24 * 60 * 60; // 86400s (24 hours)
  const VERIFY_OTP_TTL_SECONDS = 15 * 60; // 900s (15 minutes)

  await redis.set(
    `verify_email:token:${tokenHash}`,
    JSON.stringify({ userId: user.id, email: user.email }),
    'EX',
    VERIFY_TOKEN_TTL_SECONDS
  );
  await redis.set(
    `verify_email:otp:${user.email}`,
    JSON.stringify({ otpHash, userId: user.id }),
    'EX',
    VERIFY_OTP_TTL_SECONDS
  );

  logger.info(`✉️ [DEV ONLY] Email Verification Token for ${user.email}: ${rawHexToken}`);
  logger.info(`🔢 [DEV ONLY] Email Verification OTP for ${user.email}: ${rawOtp}`);

  return {
    message: 'Verification link and code have been sent to your email address.',
    devToken: rawHexToken,
    devOtp: rawOtp,
  };
};

/**
 * Verifies email address by checking the token or OTP in Redis,
 * flips `isEmailVerified: true` in PostgreSQL, and removes Redis verification keys.
 *
 * @param {Object} params
 * @param {string} params.token - 64-char URL Hex token or 6-digit OTP
 * @param {string} [params.email] - Optional email address for OTP direct lookup
 * @returns {Promise<{ user: Object, message: string }>}
 */
export const verifyEmail = async ({ token, email }) => {
  const inputHash = hashToken(token);
  let userId = null;
  let userEmail = email;

  // 1. Check if token matches a 64-character URL Hex Token
  const tokenRecordJson = await redis.get(`verify_email:token:${inputHash}`);

  if (tokenRecordJson) {
    const data = JSON.parse(tokenRecordJson);
    userId = data.userId;
    userEmail = data.email;
  } else if (email) {
    // 2. Check if token matches a 6-digit OTP for this email
    const otpRecordJson = await redis.get(`verify_email:otp:${email}`);
    if (otpRecordJson) {
      const data = JSON.parse(otpRecordJson);
      if (data.otpHash === inputHash) {
        userId = data.userId;
      }
    }
  }

  if (!userId) {
    throw new BadRequestError('Invalid or expired email verification code or link.');
  }

  // 3. Mark email as verified in PostgreSQL
  const updatedUser = await prisma.user.update({
    where: { id: userId },
    data: { isEmailVerified: true },
    select: USER_PROJECTION,
  });

  // 4. Clean up Redis verification keys
  await redis.del(`verify_email:token:${inputHash}`);
  if (userEmail) {
    await redis.del(`verify_email:otp:${userEmail}`);
  }

  logger.info(`✅ Email successfully verified for user: ${updatedUser.email}`);

  return {
    user: updatedUser,
    message: 'Your email address has been verified successfully.',
  };
};
