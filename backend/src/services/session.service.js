import { redis } from '../config/redis.js';
import { env } from '../config/env.js';
import { parseDurationToSeconds } from '../utils/jwt.js';

/**
 * Key prefix for Redis refresh token storage.
 * Key format: `auth:refresh:<userId>:<tokenHash>` or `auth:refresh:<userId>`
 */
const REFRESH_TOKEN_PREFIX = 'auth:refresh:';

/**
 * Stores a user's active Refresh Token in Redis with an automatic TTL.
 *
 * @param {string} userId - User's unique identifier.
 * @param {string} refreshToken - The raw Refresh Token string.
 * @returns {Promise<void>}
 */
export const storeRefreshToken = async (userId, refreshToken) => {
  const key = `${REFRESH_TOKEN_PREFIX}${userId}`;
  const ttlSeconds = parseDurationToSeconds(env.JWT_REFRESH_EXPIRES_IN);

  // Store in Redis with TTL in seconds (EX)
  await redis.set(key, refreshToken, 'EX', ttlSeconds);
};

/**
 * Validates if the presented Refresh Token matches the active token in Redis.
 *
 * @param {string} userId - User's unique identifier.
 * @param {string} refreshToken - The Refresh Token presented by the client.
 * @returns {Promise<boolean>} True if valid and matches whitelist, false otherwise.
 */
export const validateStoredRefreshToken = async (userId, refreshToken) => {
  const key = `${REFRESH_TOKEN_PREFIX}${userId}`;
  const storedToken = await redis.get(key);

  if (!storedToken) {
    return false;
  }

  return storedToken === refreshToken;
};

/**
 * Revokes a user's active session by deleting their Refresh Token from Redis.
 *
 * @param {string} userId - User's unique identifier.
 * @returns {Promise<void>}
 */
export const revokeRefreshToken = async (userId) => {
  const key = `${REFRESH_TOKEN_PREFIX}${userId}`;
  await redis.del(key);
};
