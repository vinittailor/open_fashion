import * as adminService from '../services/admin.service.js';
import { ApiResponse } from '../utils/apiResponse.js';

/**
 * Controller: List users with search, role filters, and pagination.
 * GET /api/v1/admin/users
 */
export const listUsers = async (req, res, next) => {
  try {
    const result = await adminService.listUsers(req.query);
    return ApiResponse.success(res, 'Users retrieved successfully', result);
  } catch (error) {
    next(error);
  }
};

/**
 * Controller: Get user details by ID.
 * GET /api/v1/admin/users/:id
 */
export const getUserById = async (req, res, next) => {
  try {
    const user = await adminService.getUserById(req.params.id);
    return ApiResponse.success(res, 'User retrieved successfully', { user });
  } catch (error) {
    next(error);
  }
};

/**
 * Controller: Update user role (ADMIN / CUSTOMER).
 * PATCH /api/v1/admin/users/:id/role
 */
export const updateUserRole = async (req, res, next) => {
  try {
    const user = await adminService.updateUserRole(
      req.params.id,
      req.body.role,
      req.user.id
    );
    return ApiResponse.success(res, 'User role updated successfully', { user });
  } catch (error) {
    next(error);
  }
};

/**
 * Controller: Soft-delete/deactivate a user.
 * DELETE /api/v1/admin/users/:id
 */
export const softDeleteUser = async (req, res, next) => {
  try {
    const user = await adminService.softDeleteUser(req.params.id, req.user.id);
    return ApiResponse.success(res, 'User deactivated successfully', { user });
  } catch (error) {
    next(error);
  }
};

/**
 * Controller: Restore a deactivated user.
 * PATCH /api/v1/admin/users/:id/restore
 */
export const restoreUser = async (req, res, next) => {
  try {
    const user = await adminService.restoreUser(req.params.id);
    return ApiResponse.success(res, 'User restored successfully', { user });
  } catch (error) {
    next(error);
  }
};
