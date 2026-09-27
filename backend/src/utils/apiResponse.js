/**
 * Standardized API Response Class.
 * Enforces consistent JSON envelope formatting across all controllers.
 */
export class ApiResponse {
  constructor(statusCode, data = null, message = 'Success') {
    this.statusCode = statusCode;
    this.data = data;
    this.message = message;
    this.success = statusCode < 400;
  }

  /**
   * Static helper to directly send an HTTP 200 OK response.
   */
  static success(res, message = 'Operation completed successfully.', data = null) {
    return res.status(200).json(new ApiResponse(200, data, message));
  }

  /**
   * Static helper to directly send an HTTP 201 Created response.
   */
  static created(res, message = 'Resource created successfully.', data = null) {
    return res.status(201).json(new ApiResponse(201, data, message));
  }
}
