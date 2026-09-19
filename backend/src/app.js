import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import morgan from 'morgan';
import { env } from './config/env.js';
import { logger } from './utils/logger.js';
import { errorHandler, NotFoundError } from './middleware/errorHandler.js';

// Initialize Express 5 Application
export const app = express();

// 1. Security Middleware: HTTP Headers
app.use(helmet());

// 2. Security Middleware: Cross-Origin Resource Sharing (CORS)
app.use(
  cors({
    origin: (origin, callback) => {
      // Allow requests with no origin (e.g. mobile native apps, curl, Postman)
      if (!origin || env.CORS_ORIGIN_LIST.includes(origin)) {
        callback(null, true);
      } else {
        callback(new Error(`CORS Error: Origin ${origin} not allowed`));
      }
    },
    credentials: true,
    methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With'],
  })
);

// 3. Request Body Parsing
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// 4. HTTP Request Logging via Morgan -> Winston Stream
const morganFormat = env.isProduction ? 'combined' : ':method :url :status :res[content-length] - :response-time ms';
app.use(
  morgan(morganFormat, {
    stream: logger.stream,
    skip: (req) => req.url === '/health', // Skip logging health checks to prevent log flooding
  })
);

// 5. System Health Check Endpoint
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: env.NODE_ENV,
    version: '1.0.0',
  });
});

// 6. Base API v1 Prefix Route (Placeholder for upcoming modules)
app.get('/api/v1', (req, res) => {
  res.status(200).json({
    success: true,
    message: 'Welcome to Open Fashion Enterprise API v1',
  });
});

// 7. Catch-all 404 Route for Undefined Endpoints
app.use((req, res, next) => {
  next(new NotFoundError(`Cannot find endpoint [${req.method}] ${req.originalUrl} on this server`));
});

// 8. Central Global Error Handling Middleware
app.use(errorHandler);
