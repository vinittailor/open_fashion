import Redis from "ioredis";
import { env } from "./env.js";
import { logger } from "../utils/logger.js";

/**
 * Configure Redis Client with Exponential Backoff Retry Strategy
 */
export const redis = new Redis(env.REDIS_URL, {
  maxRetriesPerRequest: 3,
  retryStrategy(times) {
    const delay = Math.min(times * 200, 2000); // Exponential backoff capped at 2 seconds
    logger.warn(`🔄 Redis reconnecting... attempt #${times} in ${delay}ms`);
    return delay;
  },
  reconnectOnError(err) {
    const targetError = "READONLY";
    if (err.message.includes(targetError)) {
      return true; // Reconnect if Redis is in read-only slave state
    }
    return false;
  },
});

/**
 * Redis Connection Event Listeners
 */
redis.on("connect", () => {
  logger.info("🔴 Redis Client connecting...");
});

redis.on("ready", () => {
  logger.info("🚀 Redis Client ready and connected successfully");
});

redis.on("error", (err) => {
  logger.error("❌ Redis Client error:", err.message);
});

redis.on("close", () => {
  logger.warn("⚠️ Redis connection closed");
});

/**
 * Verify Redis Readiness on Server Startup
 */
export const connectRedis = async () => {
  try {
    const pong = await redis.ping();
    if (pong === "PONG") {
      logger.info("🔴 Redis connection verified via PING/PONG");
    }
  } catch (error) {
    logger.error("❌ Failed to connect to Redis:", error.message);
    // In dev we log error; in strict production you may exit or fallback to degraded mode
  }
};

/**
 * Graceful Redis Teardown on Server Termination
 */
export const disconnectRedis = async () => {
  try {
    await redis.quit();
    logger.info("🔴 Redis connection closed gracefully");
  } catch (error) {
    logger.error("❌ Error while disconnecting from Redis:", error.message);
  }
};

export default redis;
