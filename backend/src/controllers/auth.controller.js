import * as authService from '../services/auth.service.js';
import { ApiResponse } from '../utils/apiResponse.js';

export const register = async (req, res, next) => {
  try {
    const user = await authService.registerUser(req.body);

    return ApiResponse.created(res, 'Account registered successfully.', {user});
  } catch (error) {
    next(error);
  }
};
