import * as authService from '../services/auth.service.js';
import { ApiResponse } from '../utils/apiResponse.js';

/**
 * Handles customer user registration.
 * POST /api/v1/auth/register
 */
export const register = async (req, res, next) => {
  try {
    const user = await authService.registerUser(req.body);
    return ApiResponse.created(res, 'Account registered successfully.', { user });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles user login with email & password.
 * POST /api/v1/auth/login
 */
export const login = async (req, res, next) => {
  try {
    const { user, tokens } = await authService.loginUser(req.body);

    return ApiResponse.success(res, 'Login successful.', {
      user,
      ...tokens,
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles Refresh Token exchange (Token Rotation).
 * POST /api/v1/auth/refresh
 */
export const refresh = async (req, res, next) => {
  try {
    const { user, tokens } = await authService.refreshAccessToken(req.body.refreshToken);

    return ApiResponse.success(res, 'Tokens refreshed successfully.', {
      user,
      ...tokens,
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Handles user logout by revoking the active session from Redis.
 * POST /api/v1/auth/logout
 */
export const logout = async (req, res, next) => {
  try {
    // If authenticated via middleware, req.user.id is populated;
    // Otherwise fallback to decoded token or body
    const userId = req.user?.id || req.body?.userId;

    if (userId) {
      await authService.logoutUser(userId);
    }

    return ApiResponse.success(res, 'Logged out successfully.', null);
  } catch (error) {
    next(error);
  }
};
