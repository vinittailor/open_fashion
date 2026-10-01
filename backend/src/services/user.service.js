import { prisma } from '../config/prisma.js';
import { NotFoundError } from '../middleware/errorHandler.js';

// Safe User fields projection (never returns passwordHash)
const userSelectFields = {
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
 * Fetches authenticated user's profile by ID.
 *
 * @param {string} userId
 * @returns {Promise<Object>} Safe user entity
 */
export const getCurrentUserProfile = async (userId) => {
  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: userSelectFields,
  });

  if (!user) {
    throw new NotFoundError('User profile not found');
  }

  return user;
};

/**
 * Updates authenticated user's profile details.
 *
 * @param {string} userId
 * @param {Object} updateData
 * @returns {Promise<Object>} Updated safe user entity
 */
export const updateUserProfile = async (userId, updateData) => {
  const { name, phoneNumber } = updateData;

  const user = await prisma.user.update({
    where: { id: userId },
    data: {
      ...(name !== undefined && { name }),
      ...(phoneNumber !== undefined && { phoneNumber }),
    },
    select: userSelectFields,
  });

  return user;
};
