import * as userService from '../services/user.service.js';
import { ApiResponse } from '../utils/apiResponse.js';

/**
 * Handles fetching the authenticated user's profile.
 * GET /api/v1/users/me
 */
export const getMe = async (req, res, next) => {
  try {
    const user = await userService.getCurrentUserProfile(req.user.id);
    return ApiResponse.success(res, 'User profile retrieved successfully', { user });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles updating the authenticated user's profile.
 * PATCH /api/v1/users/me
 */
export const updateMe = async (req, res, next) => {
  try {
    const user = await userService.updateUserProfile(req.user.id, req.body);
    return ApiResponse.success(res, 'User profile updated successfully', { user });
  } catch (error) {
    next(error);
  }
};
