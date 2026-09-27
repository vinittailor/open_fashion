import pg from "pg";
import { PrismaPg } from "@prisma/adapter-pg";
import { PrismaClient } from "@prisma/client";
import { env } from "./env.js";
import { logger } from "../utils/logger.js";

const { Pool } = pg;

/**
 * Configure PostgreSQL Connection Pool
 */
const pool = new Pool({
  connectionString: env.DATABASE_URL,
  max: 20, // Maximum active connections in pool
  idleTimeoutMillis: 30000, // Close idle clients after 30 seconds
  connectionTimeoutMillis: 5000, // Return an error after 5 seconds if connection cannot be established
});

// Bind PostgreSQL pool to Prisma 7 driver adapter
const adapter = new PrismaPg(pool);

/**
 * Instantiate Prisma Client Singleton with Adapter & Logging
 */
export const prisma = new PrismaClient({
  adapter,
  log:
    env.NODE_ENV === "development"
      ? [
          { emit: "event", level: "query" },
          { emit: "event", level: "error" },
          { emit: "event", level: "warn" },
        ]
      : [{ emit: "event", level: "error" }],
});

// Log slow or debug queries during development
if (env.NODE_ENV === "development") {
  prisma.$on("query", (e) => {
    logger.debug(`[Prisma Query] ${e.query} - Duration: ${e.duration}ms`);
  });

  prisma.$on("warn", (e) => {
    logger.warn(`[Prisma Warning] ${e.message}`);
  });
}

prisma.$on("error", (e) => {
  logger.error(`[Prisma Error] ${e.message}`);
});

/**
 * Verify Database Connectivity on Server Startup
 */
export const connectDB = async () => {
  try {
    // Execute a lightweight ping query to verify database readiness
    await prisma.$queryRaw`SELECT 1`;
    logger.info("🐘 PostgreSQL Database connected successfully via Prisma 7");
  } catch (error) {
    logger.error("❌ Failed to connect to PostgreSQL Database:", error);
    process.exit(1);
  }
};

/**
 * Graceful Database Teardown on Server Termination
 */
export const disconnectDB = async () => {
  try {
    await prisma.$disconnect();
    await pool.end();
    logger.info("🐘 PostgreSQL connection pool closed gracefully");
  } catch (error) {
    logger.error("❌ Error while disconnecting from PostgreSQL:", error);
  }
};

export default prisma;
