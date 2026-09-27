import "dotenv/config";
import { defineConfig, env } from "prisma/config";

/**
 * Open Fashion — Prisma 7 Configuration
 * Handles environment-specific database connection & migration paths.
 */
export default defineConfig({
  schema: "prisma/schema.prisma",
  migrations: {
    path: "prisma/migrations",
  },
  datasource: {
    url: env("DATABASE_URL"),
  },
});
