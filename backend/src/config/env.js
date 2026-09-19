import { z } from 'zod';
import dotenv from 'dotenv';

// Load local .env if present
dotenv.config();

const envSchema = z.object({
  NODE_ENV: z.enum(['development', 'test', 'production']).default('development'),
  PORT: z.coerce.number().default(5000),

  // Database & Cache Connections
  DATABASE_URL: z.string().min(1, 'DATABASE_URL is required'),
  REDIS_URL: z.string().min(1, 'REDIS_URL is required').default('redis://localhost:6379'),

  // JWT Cryptographic Secrets
  JWT_SECRET: z.string().min(32, 'JWT_SECRET must be at least 32 characters long'),
  JWT_EXPIRES_IN: z.string().default('15m'),
  JWT_REFRESH_SECRET: z.string().min(32, 'JWT_REFRESH_SECRET must be at least 32 characters long'),
  JWT_REFRESH_EXPIRES_IN: z.string().default('7d'),

  // Security & Cross-Origin
  CORS_ORIGINS: z.string().default('http://localhost:3000,http://localhost:5173,http://localhost:8080'),
});

const parseEnv = () => {
  const result = envSchema.safeParse(process.env);

  if (!result.success) {
    console.error('❌ FATAL: Invalid environment variables detected on startup:');
    console.error(JSON.stringify(result.error.format(), null, 2));
    process.exit(1);
  }

  // Parse comma-separated CORS origins into a clean array
  const corsArray = result.data.CORS_ORIGINS.split(',').map((origin) => origin.trim());

  return {
    ...result.data,
    CORS_ORIGIN_LIST: corsArray,
    isProduction: result.data.NODE_ENV === 'production',
    isDevelopment: result.data.NODE_ENV === 'development',
    isTest: result.data.NODE_ENV === 'test',
  };
};

export const env = parseEnv();
