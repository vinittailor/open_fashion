import crypto from "node:crypto";

/**
 * Generates an unguessable, cryptographically secure random hexadecimal token.
 * Used for email verification links and password reset URLs.
 *
 * @param {number} [bytes=32] - Number of random bytes (32 bytes = 64 hex characters)
 * @returns {string} 64-character hexadecimal string
 */

export const generateRandomHex = (bytes = 32) => {
  return crypto.randomBytes(bytes).toString("hex");
};

/**
 * Generates a cryptographically secure 6-digit numeric OTP for mobile & SMS verification.
 * Range: 100000 to 999999 (inclusive).
 *
 * @returns {string} 6-digit numeric string (e.g. "582049")
 */
export const generateNumericOtp = () => {
  return crypto.randomInt(100000, 1000000).toString();
};

/**
 * Produces a one-way SHA-256 hash of a string or token.
 * Used to securely index and verify reset tokens and OTPs in Redis/DB without storing plain values.
 *
 * @param {string} token - Plain text token or OTP to hash
 * @returns {string} 64-character hexadecimal SHA-256 digest
 */
export const hashToken = (token) => {
  return crypto.createHash("sha256").update(token).digest("hex");
};
