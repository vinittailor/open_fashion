import { z } from 'zod';

/**
 * Password complexity regular expression:
 * - At least one uppercase letter (?=.*[A-Z])
 * - At least one lowercase letter (?=.*[a-z])
 * - At least one numeric digit (?=.*\d)
 * - At least one special character (?=.*[@$!%*?&#^()_+\-=[\]{}|;:,.<>])
 */
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=[\]{}|;:,.<>])/;

/**
 * Registration request validation schema (Zod 4 compliant).
 * Validates and sanitizes the `req.body` payload for POST /api/v1/auth/register.
 */
export const registerValidation = z.object({
  body: z.object({
    name: z
      .string({ error: 'Full name is required' })
      .trim()
      .min(2, { error: 'Full name must be at least 2 characters' })
      .max(100, { error: 'Full name cannot exceed 100 characters' }),

    email: z
      .string({ error: 'Email address is required' })
      .trim()
      .toLowerCase()
      .pipe(z.email({ error: 'Please provide a valid email address' })),

    password: z
      .string({ error: 'Password is required' })
      .min(8, { error: 'Password must be at least 8 characters long' })
      .max(72, { error: 'Password cannot exceed 72 characters' })
      .regex(passwordRegex, {
        error:
          'Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character',
      }),

    phoneNumber: z
      .string({ error: 'Phone number must be a string' })
      .trim()
      .pipe(
        z.e164({
          error: 'Please provide a valid E.164 format phone number (e.g. +1234567890)',
        })
      )
      .optional(),
  }),
});

/**
 * Login request validation schema.
 * Validates and sanitizes the `req.body` payload for POST /api/v1/auth/login.
 */
export const loginValidation = z.object({
  body: z.object({
    email: z
      .string({ error: 'Email address is required' })
      .trim()
      .toLowerCase()
      .pipe(z.email({ error: 'Please provide a valid email address' })),

    password: z
      .string({ error: 'Password is required' })
      .min(1, { error: 'Password cannot be empty' }),
  }),
});

/**
 * Refresh Token request validation schema.
 * Validates the `req.body` payload for POST /api/v1/auth/refresh.
 */
export const refreshTokenValidation = z.object({
  body: z.object({
    refreshToken: z
      .string({ error: 'Refresh token is required' })
      .min(1, { error: 'Refresh token cannot be empty' }),
  }),
});
