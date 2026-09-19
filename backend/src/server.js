import http from 'http';
import { Server as SocketIOServer } from 'socket.io';
import { app } from './app.js';
import { env } from './config/env.js';
import { logger } from './utils/logger.js';

// 1. Create native Node.js HTTP server wrapping Express app
const server = http.createServer(app);

// 2. Initialize Socket.io server attached to HTTP server
export const io = new SocketIOServer(server, {
  cors: {
    origin: env.CORS_ORIGIN_LIST,
    credentials: true,
  },
  pingTimeout: 60000,
});

// 3. Socket.io Connection & Lifecycle Event Gateway
io.on('connection', (socket) => {
  logger.info(`🔌 Real-time client connected: [Socket ID: ${socket.id}]`);

  socket.on('disconnect', (reason) => {
    logger.info(`🔌 Client disconnected: [Socket ID: ${socket.id}] - Reason: ${reason}`);
  });
});

// 4. Start HTTP & WebSocket Server Listener
server.listen(env.PORT, () => {
  logger.info(`🚀 Open Fashion Backend running in [${env.NODE_ENV}] mode on http://localhost:${env.PORT}`);
  logger.info(`📡 WebSocket Gateway initialized and listening for connections`);
  logger.info(`🩺 Health check accessible at: http://localhost:${env.PORT}/health`);
});

// 5. Graceful Shutdown Handler (Production Resilience)
const gracefulShutdown = (signal) => {
  logger.warn(`🛑 Received ${signal}. Starting graceful shutdown...`);

  server.close(() => {
    logger.info('🔒 HTTP server closed successfully.');
    io.close(() => {
      logger.info('🔒 Socket.io server closed.');
      process.exit(0);
    });
  });

  // Force exit after 10 seconds if connections refuse to close
  setTimeout(() => {
    logger.error('⚠️ Forcefully terminating process after 10s timeout.');
    process.exit(1);
  }, 10000);
};

// Listen for termination signals
process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
process.on('SIGINT', () => gracefulShutdown('SIGINT'));
