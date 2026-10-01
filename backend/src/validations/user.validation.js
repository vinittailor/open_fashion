import { z } from 'zod';

/**
 * Validation schema for updating user profile: PATCH /api/v1/users/me (Zod 4 compliant).
 */
export const updateProfileValidation = z.object({
  body: z.object({
    name: z
      .string({ error: 'Name must be a string' })
      .trim()
      .min(2, { error: 'Name must be at least 2 characters' })
      .max(100, { error: 'Name cannot exceed 100 characters' })
      .optional(),

    phoneNumber: z
      .string({ error: 'Phone number must be a string' })
      .trim()
      .pipe(
        z.e164({
          error: 'Please provide a valid E.164 format phone number (e.g. +1234567890)',
        })
      )
      .optional()
      .nullable(),
  }),
});
