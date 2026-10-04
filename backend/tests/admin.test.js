import { describe, it, expect, beforeAll, vi } from 'vitest';
import request from 'supertest';
import { app } from '../src/app.js';
import { signAccessToken } from '../src/utils/jwt.js';

const { mockCustomer, mockAdmin } = vi.hoisted(() => {
  const customer = {
    id: '22222222-2222-4222-a222-222222222222',
    name: 'Customer Test',
    email: 'customer@openfashion.com',
    role: 'CUSTOMER',
    phoneNumber: '+1234567890',
    isEmailVerified: true,
    deletedAt: null,
    createdAt: new Date(),
    updatedAt: new Date(),
    avatar: null,
    _count: { orders: 2, reviews: 1 },
  };

  const admin = {
    id: '11111111-1111-4111-a111-111111111111',
    name: 'Admin Test',
    email: 'admin@openfashion.com',
    role: 'ADMIN',
    phoneNumber: '+1987654321',
    isEmailVerified: true,
    deletedAt: null,
    createdAt: new Date(),
    updatedAt: new Date(),
    avatar: null,
    _count: { orders: 0, reviews: 0 },
  };

  return { mockCustomer: customer, mockAdmin: admin };
});

// Mock Redis to prevent connection retries in test environment
vi.mock('../src/config/redis.js', () => ({
  redis: {
    get: vi.fn().mockResolvedValue(null),
    set: vi.fn().mockResolvedValue('OK'),
    del: vi.fn().mockResolvedValue(1),
    on: vi.fn(),
  },
  connectRedis: vi.fn().mockResolvedValue(),
  disconnectRedis: vi.fn().mockResolvedValue(),
}));

// Mock Prisma for deterministic, isolated unit testing
vi.mock('../src/config/prisma.js', () => ({
  prisma: {
    $transaction: vi.fn().mockImplementation((queries) => Promise.all(queries)),
    user: {
      count: vi.fn().mockResolvedValue(1),
      findMany: vi.fn().mockResolvedValue([mockCustomer]),
      findUnique: vi.fn().mockImplementation(({ where }) => {
        if (where.id === mockCustomer.id) {
          return Promise.resolve(mockCustomer);
        }
        if (where.id === mockAdmin.id) {
          return Promise.resolve(mockAdmin);
        }
        return Promise.resolve(null);
      }),
      update: vi.fn().mockImplementation(({ where, data }) => {
        const base = where.id === mockAdmin.id ? mockAdmin : mockCustomer;
        Object.assign(base, data);
        return Promise.resolve({ ...base });
      }),
    },
    $on: vi.fn(),
  },
}));

describe('Admin User Management Endpoints (Micro-Slice 1.5.1)', () => {
  let adminToken;
  let customerToken;
  const adminId = '11111111-1111-4111-a111-111111111111';
  const customerId = '22222222-2222-4222-a222-222222222222';
  const unknownId = '99999999-9999-4999-a999-999999999999';

  beforeAll(() => {
    adminToken = signAccessToken({
      id: adminId,
      email: 'admin@openfashion.com',
      role: 'ADMIN',
    });

    customerToken = signAccessToken({
      id: customerId,
      email: 'customer@openfashion.com',
      role: 'CUSTOMER',
    });
  });

  describe('Security & RBAC Guards', () => {
    it('should return 401 Unauthorized when no token is provided', async () => {
      const res = await request(app).get('/api/v1/admin/users');
      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
    });

    it('should return 403 Forbidden when a non-admin (CUSTOMER) accesses the endpoint', async () => {
      const res = await request(app)
        .get('/api/v1/admin/users')
        .set('Authorization', `Bearer ${customerToken}`);

      expect(res.status).toBe(403);
      expect(res.body.success).toBe(false);
      expect(res.body.error.message).toMatch(/Access denied/i);
    });
  });

  describe('Input Validation & Format Checks', () => {
    it('should reject invalid UUID on GET /api/v1/admin/users/:id with 422', async () => {
      const res = await request(app)
        .get('/api/v1/admin/users/invalid-uuid-123')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });

    it('should reject invalid role payload on PATCH /api/v1/admin/users/:id/role with 422', async () => {
      const res = await request(app)
        .patch(`/api/v1/admin/users/${customerId}/role`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ role: 'SUPER_HERO' });

      expect(res.status).toBe(422);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('VALIDATION_ERROR');
    });
  });

  describe('Admin Operations (CRUD, Roles & Soft-Delete)', () => {
    it('should list users with pagination metadata on GET /api/v1/admin/users', async () => {
      const res = await request(app)
        .get('/api/v1/admin/users?page=1&limit=5&status=all&sortBy=createdAt&sortOrder=desc')
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.users).toBeInstanceOf(Array);
      expect(res.body.data.pagination).toHaveProperty('totalUsers');
      expect(res.body.data.pagination.currentPage).toBe(1);
    });

    it('should get single user details by ID on GET /api/v1/admin/users/:id', async () => {
      const res = await request(app)
        .get(`/api/v1/admin/users/${customerId}`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.user.id).toBe(customerId);
    });

    it('should return 404 when user is not found on GET /api/v1/admin/users/:id', async () => {
      const res = await request(app)
        .get(`/api/v1/admin/users/${unknownId}`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(404);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('NOT_FOUND');
    });

    it('should update user role to ADMIN on PATCH /api/v1/admin/users/:id/role', async () => {
      const res = await request(app)
        .patch(`/api/v1/admin/users/${customerId}/role`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ role: 'ADMIN' });

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.user.role).toBe('ADMIN');
    });

    it('should prevent admin from demoting their own role with 400', async () => {
      const res = await request(app)
        .patch(`/api/v1/admin/users/${adminId}/role`)
        .set('Authorization', `Bearer ${adminToken}`)
        .send({ role: 'CUSTOMER' });

      expect(res.status).toBe(400);
      expect(res.body.success).toBe(false);
      expect(res.body.error.message).toMatch(/cannot demote their own account/i);
    });

    it('should prevent admin from deactivating/deleting their own account with 400', async () => {
      const res = await request(app)
        .delete(`/api/v1/admin/users/${adminId}`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(400);
      expect(res.body.success).toBe(false);
      expect(res.body.error.message).toMatch(/cannot deactivate their own account/i);
    });

    it('should soft-delete/deactivate user on DELETE /api/v1/admin/users/:id', async () => {
      const res = await request(app)
        .delete(`/api/v1/admin/users/${customerId}`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.user).toBeDefined();
    });

    it('should restore a deactivated user on PATCH /api/v1/admin/users/:id/restore', async () => {
      // Set deletedAt on customer
      mockCustomer.deletedAt = new Date();

      const res = await request(app)
        .patch(`/api/v1/admin/users/${customerId}/restore`)
        .set('Authorization', `Bearer ${adminToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.user.deletedAt).toBeNull();
    });
  });
});
