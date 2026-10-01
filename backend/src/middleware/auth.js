import { verifyAccessToken } from "../utils/jwt.js";
import { UnauthorizedError, ForbiddenError } from "./errorHandler.js";

/**
 * Express Middleware: Verifies JWT Access Token from `Authorization: Bearer <token>` header.
 * Attaches the decoded payload `{ id, email, role }` to `req.user`.
 */
export const authenticate = (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      throw new UnauthorizedError("Authentication token missing or malformed");
    }

    const token = authHeader.split(" ")[1];
    if (!token) {
      throw new UnauthorizedError("Authentication token missing");
    }

    // verifyAccessToken throws JsonWebTokenError or TokenExpiredError if invalid
    const decoded = verifyAccessToken(token);

    // Attach verified user payload to request
    req.user = {
      id: decoded.id,
      email: decoded.email,
      role: decoded.role,
    };

    next();
  } catch (error) {
    next(error);
  }
};

/**
 * Express Middleware Factory: Enforces Role-Based Access Control (RBAC).
 * Checks if `req.user.role` is included in the allowed roles list.
 *
 * @param  {...string} allowedRoles Roles permitted to access the route (e.g. 'ADMIN', 'SUPERADMIN')
 */
export const authorize = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return next(new UnauthorizedError("Authentication required"));
    }

    if (!allowedRoles.includes(req.user.role)) {
      return next(
        new ForbiddenError(
          `Access denied. Requires one of the following roles: [${allowedRoles.join(", ")}]`,
        ),
      );
    }

    next();
  };
};
