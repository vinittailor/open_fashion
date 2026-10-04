import { prisma } from '../config/prisma.js';
import { NotFoundError, BadRequestError } from '../middleware/errorHandler.js';

// Safe User fields projection for Admin queries (never returns password hashes or raw reset tokens)
export const safeAdminUserSelect = {
  id: true,
  name: true,
  email: true,
  role: true,
  phoneNumber: true,
  isEmailVerified: true,
  deletedAt: true,
  createdAt: true,
  updatedAt: true,
  avatar: {
    select: {
      id: true,
      url: true,
      filename: true,
    },
  },
};

/**
 * Retrieves a paginated and filtered list of users for the admin dashboard.
 *
 * @param {Object} queryOptions
 * @param {number} queryOptions.page
 * @param {number} queryOptions.limit
 * @param {string} [queryOptions.search]
 * @param {'ADMIN'|'CUSTOMER'} [queryOptions.role]
 * @param {'all'|'active'|'deleted'} [queryOptions.status]
 * @param {'createdAt'|'name'|'email'|'updatedAt'} [queryOptions.sortBy]
 * @param {'asc'|'desc'} [queryOptions.sortOrder]
 * @returns {Promise<{ users: Array, pagination: Object }>}
 */
export const listUsers = async ({
  page = 1,
  limit = 10,
  search,
  role,
  status = 'active',
  sortBy = 'createdAt',
  sortOrder = 'desc',
}) => {
  const pageNum = Math.max(1, parseInt(page, 10) || 1);
  const limitNum = Math.max(1, Math.min(100, parseInt(limit, 10) || 10));
  const skip = (pageNum - 1) * limitNum;
  const take = limitNum;

  // Build dynamic Prisma where clause
  const where = {};

  // 1. Search across name or email
  if (search) {
    where.OR = [
      { name: { contains: search, mode: 'insensitive' } },
      { email: { contains: search, mode: 'insensitive' } },
    ];
  }

  // 2. Filter by Role
  if (role) {
    where.role = role;
  }

  // 3. Filter by Active / Deleted status
  if (status === 'active') {
    where.deletedAt = null;
  } else if (status === 'deleted') {
    where.deletedAt = { not: null };
  }
  // if 'all', we don't apply any deletedAt constraint

  // Execute count and query in parallel via transaction
  const [totalUsers, users] = await prisma.$transaction([
    prisma.user.count({ where }),
    prisma.user.findMany({
      where,
      skip,
      take,
      orderBy: { [sortBy]: sortOrder },
      select: {
        ...safeAdminUserSelect,
        _count: {
          select: {
            orders: true,
            reviews: true,
          },
        },
      },
    }),
  ]);

  const totalPages = Math.ceil(totalUsers / limitNum) || 1;

  return {
    users,
    pagination: {
      totalUsers,
      totalPages,
      currentPage: pageNum,
      limit: limitNum,
      hasNextPage: pageNum < totalPages,
      hasPrevPage: pageNum > 1,
    },
  };
};

/**
 * Fetches single user by ID with relational counts.
 *
 * @param {string} userId
 * @returns {Promise<Object>}
 */
export const getUserById = async (userId) => {
  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: {
      ...safeAdminUserSelect,
      profile: true,
      _count: {
        select: {
          orders: true,
          reviews: true,
          addresses: true,
        },
      },
    },
  });

  if (!user) {
    throw new NotFoundError(`User with ID ${userId} not found`);
  }

  return user;
};

/**
 * Updates a user's role (e.g. promoting customer to admin, or vice versa).
 * Prevents admins from accidentally demoting themselves.
 *
 * @param {string} targetUserId
 * @param {'ADMIN'|'CUSTOMER'} newRole
 * @param {string} currentAdminId
 * @returns {Promise<Object>}
 */
export const updateUserRole = async (targetUserId, newRole, currentAdminId) => {
  if (targetUserId === currentAdminId && newRole !== 'ADMIN') {
    throw new BadRequestError('Administrators cannot demote their own account');
  }

  const user = await prisma.user.findUnique({
    where: { id: targetUserId },
  });

  if (!user) {
    throw new NotFoundError(`User with ID ${targetUserId} not found`);
  }

  const updatedUser = await prisma.user.update({
    where: { id: targetUserId },
    data: { role: newRole },
    select: safeAdminUserSelect,
  });

  return updatedUser;
};

/**
 * Soft deletes a user account by setting `deletedAt` timestamp.
 *
 * @param {string} targetUserId
 * @param {string} currentAdminId
 * @returns {Promise<Object>}
 */
export const softDeleteUser = async (targetUserId, currentAdminId) => {
  if (targetUserId === currentAdminId) {
    throw new BadRequestError('Administrators cannot deactivate their own account');
  }

  const user = await prisma.user.findUnique({
    where: { id: targetUserId },
  });

  if (!user) {
    throw new NotFoundError(`User with ID ${targetUserId} not found`);
  }

  if (user.deletedAt !== null) {
    throw new BadRequestError('User account is already deactivated');
  }

  const deactivatedUser = await prisma.user.update({
    where: { id: targetUserId },
    data: { deletedAt: new Date() },
    select: safeAdminUserSelect,
  });

  return deactivatedUser;
};

/**
 * Restores a soft-deleted user account by clearing `deletedAt`.
 *
 * @param {string} targetUserId
 * @returns {Promise<Object>}
 */
export const restoreUser = async (targetUserId) => {
  const user = await prisma.user.findUnique({
    where: { id: targetUserId },
  });

  if (!user) {
    throw new NotFoundError(`User with ID ${targetUserId} not found`);
  }

  if (user.deletedAt === null) {
    throw new BadRequestError('User account is already active');
  }

  const restoredUser = await prisma.user.update({
    where: { id: targetUserId },
    data: { deletedAt: null },
    select: safeAdminUserSelect,
  });

  return restoredUser;
};
