import bcrypt from 'bcryptjs';

/**
 * Default cost factor (work factor) for Bcrypt salt generation.
 * 12 rounds provides a balance of cryptographic hardness and sub-second CPU response time.
 */
const SALT_ROUNDS = 12;

/**
 * Hashes a plaintext password using a cryptographically secure random salt.
 *
 * @param {string} plaintextPassword - The raw user password to hash.
 * @returns {Promise<string>} - Resolves with the resulting Bcrypt hash string.
 * @throws {Error} If the plaintext password is empty or hashing fails.
 */
export const hashPassword = async (plaintextPassword) => {
  if (!plaintextPassword || typeof plaintextPassword !== 'string') {
    throw new Error('hashPassword requires a valid non-empty string.');
  }

  const salt = await bcrypt.genSalt(SALT_ROUNDS);
  return bcrypt.hash(plaintextPassword, salt);
};

/**
 * Compares a plaintext password against a stored Bcrypt hash using constant-time comparison.
 *
 * @param {string} plaintextPassword - The raw password provided at login.
 * @param {string} hashedPassword - The hashed password stored in the database.
 * @returns {Promise<boolean>} - Resolves with true if matching, false otherwise.
 */
export const comparePassword = async (plaintextPassword, hashedPassword) => {
  if (!plaintextPassword || !hashedPassword) {
    return false;
  }

  return bcrypt.compare(plaintextPassword, hashedPassword);
};
