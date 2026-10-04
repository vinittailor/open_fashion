import { Router } from 'express';
import {
  listUsers,
  getUserById,
  updateUserRole,
  softDeleteUser,
  restoreUser,
} from '../controllers/admin.controller.js';
import { authenticate, authorize } from '../middleware/auth.js';
import { validate } from '../middleware/validate.js';
import {
  listUsersQueryValidation,
  userIdParamValidation,
  updateUserRoleValidation,
} from '../validations/admin.validation.js';

const router = Router();

// All Admin routes require authentication AND the ADMIN role
router.use(authenticate, authorize('ADMIN'));

/**
 * @route   GET /api/v1/admin/users
 * @desc    List all users with search, role filters, status filters, and pagination
 * @access  Private (ADMIN)
 */
router.get('/users', validate(listUsersQueryValidation), listUsers);

/**
 * @route   GET /api/v1/admin/users/:id
 * @desc    Get user details by ID
 * @access  Private (ADMIN)
 */
router.get('/users/:id', validate(userIdParamValidation), getUserById);

/**
 * @route   PATCH /api/v1/admin/users/:id/role
 * @desc    Update a user's role (ADMIN / CUSTOMER)
 * @access  Private (ADMIN)
 */
router.patch('/users/:id/role', validate(updateUserRoleValidation), updateUserRole);

/**
 * @route   DELETE /api/v1/admin/users/:id
 * @desc    Soft-delete / Deactivate a user account
 * @access  Private (ADMIN)
 */
router.delete('/users/:id', validate(userIdParamValidation), softDeleteUser);

/**
 * @route   PATCH /api/v1/admin/users/:id/restore
 * @desc    Restore a deactivated user account
 * @access  Private (ADMIN)
 */
router.patch('/users/:id/restore', validate(userIdParamValidation), restoreUser);

export default router;
