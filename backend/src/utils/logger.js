import winston from 'winston';
import { env } from '../config/env.js';

const { combine, timestamp, printf, colorize, json, errors, splat } = winston.format;

// Custom human-readable format for local development terminal
const devFormat = printf(({ level, message, timestamp: time, stack, ...metadata }) => {
  let log = `[${time}] ${level}: ${message}`;

  if (stack) {
    log += `\n${stack}`;
  }

  // Filter out internal symbols and format remaining metadata
  const cleanMetadata = { ...metadata };
  delete cleanMetadata.splat;
  
  if (Object.keys(cleanMetadata).length > 0) {
    log += ` | ${JSON.stringify(cleanMetadata)}`;
  }

  return log;
});

// Configure Winston logger instance
export const logger = winston.createLogger({
  level: env.isDevelopment ? 'debug' : 'info',
  format: combine(
    errors({ stack: true }), // Captures Error instances and stack traces
    splat(), // Enables string interpolation (e.g. logger.info('User %d', id))
    timestamp({ format: 'YYYY-MM-DD HH:mm:ss' })
  ),
  transports: [
    new winston.transports.Console({
      format: env.isProduction
        ? json() // Structured JSON in production for log aggregators
        : combine(colorize({ all: true }), devFormat), // Colorized readable logs in dev
      handleExceptions: true,
      handleRejections: true,
    }),
  ],
  exitOnError: false, // Do not exit automatically on handled errors
});

// Stream adapter for Morgan HTTP request logging
logger.stream = {
  write: (message) => {
    logger.http ? logger.http(message.trim()) : logger.info(message.trim());
  },
};

