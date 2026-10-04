import { ZodError } from 'zod';
import { logger } from '../utils/logger.js';
import { env } from '../config/env.js';

// Base Custom Application Error Class
export class AppError extends Error {
  constructor(message, statusCode = 500, errorCode = 'INTERNAL_ERROR', details = null) {
    super(message);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.details = details;
    this.isOperational = true; // Distinguishes operational errors from programming bugs

    Error.captureStackTrace(this, this.constructor);
  }
}

// Concrete Specialized Error Subclasses
export class BadRequestError extends AppError {
  constructor(message = 'Bad Request', details = null) {
    super(message, 400, 'BAD_REQUEST', details);
  }
}

export class UnauthorizedError extends AppError {
  constructor(message = 'Authentication required', details = null) {
    super(message, 401, 'UNAUTHORIZED', details);
  }
}

export class ForbiddenError extends AppError {
  constructor(message = 'Permission denied', details = null) {
    super(message, 403, 'FORBIDDEN', details);
  }
}

export class NotFoundError extends AppError {
  constructor(message = 'Resource not found', details = null) {
    super(message, 404, 'NOT_FOUND', details);
  }
}

export class ConflictError extends AppError {
  constructor(message = 'Resource conflict', details = null) {
    super(message, 409, 'CONFLICT', details);
  }
}

export class ValidationError extends AppError {
  constructor(message = 'Validation failed', details = null) {
    super(message, 422, 'VALIDATION_ERROR', details);
  }
}

// Global Express 5 Error Middleware (4 parameters required by Express to identify error handler)
export const errorHandler = (err, req, res, next) => {
  let statusCode = err.statusCode || 500;
  let errorCode = err.errorCode || 'INTERNAL_SERVER_ERROR';
  let message = err.message || 'An unexpected error occurred';
  let details = err.details || null;

  // 1. Handle Zod Validation Errors
  if (err instanceof ZodError || err.name === 'ZodError') {
    statusCode = 422;
    errorCode = 'VALIDATION_ERROR';
    message = 'Request validation failed';
    const issues = err.issues || err.errors || [];
    details = issues.map((e) => ({
      field: Array.isArray(e.path) ? e.path.join('.') : String(e.path || ''),
      message: e.message,
    }));
  }

  // 2. Handle Prisma Database Known Request Errors
  else if (err.code === 'P2002') {
    // Unique constraint violation (e.g. duplicate email)
    statusCode = 409;
    errorCode = 'DUPLICATE_ENTRY';
    const target = err.meta?.target ? ` on field: ${err.meta.target}` : '';
    message = `A resource with this identifier already exists${target}`;
  } else if (err.code === 'P2025') {
    // Record not found during update/delete
    statusCode = 404;
    errorCode = 'NOT_FOUND';
    message = 'The requested record was not found in database';
  }

  // 3. Handle JSON Web Token Errors
  else if (err.name === 'JsonWebTokenError') {
    statusCode = 401;
    errorCode = 'INVALID_TOKEN';
    message = 'Invalid authentication token';
  } else if (err.name === 'TokenExpiredError') {
    statusCode = 401;
    errorCode = 'TOKEN_EXPIRED';
    message = 'Authentication token has expired';
  }

  // 4. Log appropriately based on severity
  if (statusCode >= 500) {
    logger.error(`[${req.method}] ${req.originalUrl} - 500 Internal Error: ${err.message}`, {
      stack: err.stack,
      ip: req.ip,
      body: req.body,
    });
  } else {
    logger.warn(`[${req.method}] ${req.originalUrl} - ${statusCode} [${errorCode}]: ${message}`);
  }

  // 5. Send Unified JSON Error Envelope
  res.status(statusCode).json({
    success: false,
    error: {
      code: errorCode,
      message,
      ...(details && { details }),
      ...(env.isDevelopment && statusCode >= 500 && { stack: err.stack }),
    },
  });
};
