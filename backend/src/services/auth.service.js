import { prisma } from '../config/prisma.js';
import { hashPassword } from '../utils/password.js';
import { ConflictError } from '../middleware/errorHandler.js';

/**
 * Registers a new customer account in the database.
 *
 * @param {Object} userData - Validated registration payload.
 * @param {string} userData.name - User's full name.
 * @param {string} userData.email - User's normalized email address.
 * @param {string} userData.password - User's raw plaintext password.
 * @param {string} [userData.phoneNumber] - User's optional phone number.
 * @returns {Promise<Object>} Sanitized user record (excluding passwordHash).
 * @throws {ConflictError} If an account with the specified email already exists.
 */
export const registerUser = async ({ name, email, password, phoneNumber }) => {
  // 1. Proactive uniqueness check
  const existingUser = await prisma.user.findUnique({
    where: { email },
    select: { id: true },
  });

  if (existingUser) {
    throw new ConflictError('An account with this email address already exists.');
  }

  // 2. Hash raw password
  const passwordHash = await hashPassword(password);

  // 3. Persist new user in PostgreSQL via Prisma 7
  const newUser = await prisma.user.create({
    data: {
      name,
      email,
      passwordHash,
      phoneNumber: phoneNumber || null,
      role: 'CUSTOMER',
    },
    // Explicit projection: never select or expose passwordHash
    select: {
      id: true,
      name: true,
      email: true,
      role: true,
      phoneNumber: true,
      isEmailVerified: true,
      createdAt: true,
      updatedAt: true,
    },
  });

  return newUser;
};
