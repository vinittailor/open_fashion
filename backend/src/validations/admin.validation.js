import { z } from 'zod';

/**
 * Validation schema for listing users with filters and pagination:
 * GET /api/v1/admin/users (Zod 4 compliant)
 */
export const listUsersQueryValidation = z.object({
  query: z.object({
    page: z.coerce
      .number({ error: 'Page must be a valid number' })
      .int()
      .positive({ error: 'Page must be greater than 0' })
      .default(1),

    limit: z.coerce
      .number({ error: 'Limit must be a valid number' })
      .int()
      .positive({ error: 'Limit must be greater than 0' })
      .max(100, { error: 'Limit cannot exceed 100 items per page' })
      .default(10),

    search: z
      .string({ error: 'Search term must be a string' })
      .trim()
      .optional(),

    role: z
      .enum(['ADMIN', 'CUSTOMER'], {
        error: 'Role filter must be ADMIN or CUSTOMER',
      })
      .optional(),

    status: z
      .enum(['all', 'active', 'deleted'], {
        error: 'Status filter must be all, active, or deleted',
      })
      .default('active'),

    sortBy: z
      .enum(['createdAt', 'name', 'email', 'updatedAt'], {
        error: 'Sort field must be createdAt, name, email, or updatedAt',
      })
      .default('createdAt'),

    sortOrder: z
      .enum(['asc', 'desc'], {
        error: 'Sort order must be asc or desc',
      })
      .default('desc'),
  }),
});

/**
 * Validation schema for UUID parameter in admin endpoints:
 * e.g. /api/v1/admin/users/:id
 */
export const userIdParamValidation = z.object({
  params: z.object({
    id: z
      .string({ error: 'User ID parameter is required' })
      .trim()
      .pipe(z.uuid({ error: 'Invalid user UUID format' })),
  }),
});

/**
 * Validation schema for updating a user's role:
 * PATCH /api/v1/admin/users/:id/role
 */
export const updateUserRoleValidation = z.object({
  params: z.object({
    id: z
      .string({ error: 'User ID parameter is required' })
      .trim()
      .pipe(z.uuid({ error: 'Invalid user UUID format' })),
  }),
  body: z.object({
    role: z.enum(['ADMIN', 'CUSTOMER'], {
      error: 'Role must be either ADMIN or CUSTOMER',
    }),
  }),
});
