import jwt from 'jsonwebtoken';
import { env } from '../config/env.js';

/**
 * Parses time strings like '15m', '7d', '1h', '30s' into integer seconds.
 *
 * @param {string} durationStr - Formatted duration string.
 * @returns {number} Integer seconds.
 */
export const parseDurationToSeconds = (durationStr) => {
  if (typeof durationStr === 'number') return durationStr;
  if (!durationStr || typeof durationStr !== 'string') return 900;

  const match = durationStr.match(/^(\d+)([smhd])$/);
  if (!match) return 900;

  const value = parseInt(match[1], 10);
  const unit = match[2];

  switch (unit) {
    case 's':
      return value;
    case 'm':
      return value * 60;
    case 'h':
      return value * 3600;
    case 'd':
      return value * 86400;
    default:
      return 900;
  }
};

/**
 * Signs a short-lived Access Token (default: 15 minutes).
 *
 * @param {Object} payload - User claims to embed in the token.
 * @param {string} payload.id - User's UUID.
 * @param {string} payload.email - User's email.
 * @param {string} payload.role - User's role (CUSTOMER | ADMIN).
 * @returns {string} Signed JWT Access Token string.
 */
export const signAccessToken = (payload) => {
  return jwt.sign(payload, env.JWT_SECRET, {
    expiresIn: env.JWT_EXPIRES_IN,
  });
};

/**
 * Signs a long-lived Refresh Token (default: 7 days).
 *
 * @param {Object} payload - Minimal claims (typically user id & tokenId).
 * @param {string} payload.id - User's UUID.
 * @returns {string} Signed JWT Refresh Token string.
 */
export const signRefreshToken = (payload) => {
  return jwt.sign(payload, env.JWT_REFRESH_SECRET, {
    expiresIn: env.JWT_REFRESH_EXPIRES_IN,
  });
};

/**
 * Verifies and decodes an Access Token.
 *
 * @param {string} token - Raw Bearer JWT token string.
 * @returns {Object} Decoded payload.
 * @throws {jwt.JsonWebTokenError|jwt.TokenExpiredError} If token is invalid or expired.
 */
export const verifyAccessToken = (token) => {
  return jwt.verify(token, env.JWT_SECRET);
};

/**
 * Verifies and decodes a Refresh Token.
 *
 * @param {string} token - Raw Refresh JWT token string.
 * @returns {Object} Decoded payload.
 * @throws {jwt.JsonWebTokenError|jwt.TokenExpiredError} If token is invalid or expired.
 */
export const verifyRefreshToken = (token) => {
  return jwt.verify(token, env.JWT_REFRESH_SECRET);
};

/**
 * Generates a complete Auth Token pair for a user session.
 *
 * @param {Object} user - User record from database.
 * @param {string} user.id - User's UUID.
 * @param {string} user.email - User's email.
 * @param {string} user.role - User's role.
 * @returns {{ accessToken: string, refreshToken: string, expiresIn: number, refreshExpiresIn: number }}
 */
export const generateAuthTokens = (user) => {
  const claims = {
    id: user.id,
    email: user.email,
    role: user.role,
  };

  const accessToken = signAccessToken(claims);
  const refreshToken = signRefreshToken({ id: user.id });

  return {
    accessToken,
    refreshToken,
    expiresIn: parseDurationToSeconds(env.JWT_EXPIRES_IN), // Numeric seconds (e.g. 900)
    refreshExpiresIn: parseDurationToSeconds(env.JWT_REFRESH_EXPIRES_IN), // Numeric seconds (e.g. 604800)
  };
};
