import { prisma } from '../config/prisma.js';
import { hashPassword, comparePassword } from '../utils/password.js';
import { generateAuthTokens, verifyRefreshToken } from '../utils/jwt.js';
import {
  storeRefreshToken,
  validateStoredRefreshToken,
  revokeRefreshToken,
} from './session.service.js';
import { ConflictError, UnauthorizedError } from '../middleware/errorHandler.js';

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
