import { describe, it, expect, beforeAll, vi } from 'vitest';
import request from 'supertest';
import { app } from '../src/app.js';
import { signAccessToken } from '../src/utils/jwt.js';

const { mockFileRecord } = vi.hoisted(() => {
  const file = {
    id: '33333333-3333-4333-a333-333333333333',
    filename: 'img-test-123.jpg',
    key: 'general/img-test-123.jpg',
    url: 'http://127.0.0.1:5000/uploads/general/img-test-123.jpg',
    mimeType: 'image/jpeg',
    sizeBytes: 1024,
    provider: 'LOCAL',
    isPublic: true,
    uploaderId: '11111111-1111-4111-a111-111111111111',
    createdAt: new Date(),
    updatedAt: new Date(),
    uploader: {
      id: '11111111-1111-4111-a111-111111111111',
      name: 'Owner User',
      email: 'owner@openfashion.com',
    },
  };

  return { mockFileRecord: file };
});

// Mock Redis
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

// Mock Prisma
vi.mock('../src/config/prisma.js', () => ({
  prisma: {
    file: {
      create: vi.fn().mockImplementation(({ data }) => {
        return Promise.resolve({
          ...mockFileRecord,
          ...data,
          id: mockFileRecord.id,
        });
      }),
      findUnique: vi.fn().mockImplementation(({ where }) => {
        if (where.id === mockFileRecord.id) {
          return Promise.resolve(mockFileRecord);
        }
        return Promise.resolve(null);
      }),
      delete: vi.fn().mockImplementation(({ where }) => {
        if (where.id === mockFileRecord.id) {
          return Promise.resolve(mockFileRecord);
        }
        return Promise.resolve(null);
      }),
    },
    $on: vi.fn(),
  },
}));

describe('File Upload & Media Registry Endpoints (Micro-Slice 2.1.1)', () => {
  let userToken;
  let otherUserToken;
  const userId = '11111111-1111-4111-a111-111111111111';
  const otherUserId = '22222222-2222-4222-a222-222222222222';

  beforeAll(() => {
    userToken = signAccessToken({
      id: userId,
      email: 'owner@openfashion.com',
      role: 'CUSTOMER',
    });

    otherUserToken = signAccessToken({
      id: otherUserId,
      email: 'other@openfashion.com',
      role: 'CUSTOMER',
    });
  });

  describe('Security & Authentication Guards', () => {
    it('should return 401 Unauthorized when uploading without token', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload')
        .attach('file', Buffer.from('fake image content'), 'test.jpg');

      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
    });
  });

  describe('Single File Upload (POST /api/v1/files/upload)', () => {
    it('should successfully upload a valid JPEG image and return 201 with public URL', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload')
        .set('Authorization', `Bearer ${userToken}`)
        .attach('file', Buffer.from('fake image data'), {
          filename: 'photo.jpg',
          contentType: 'image/jpeg',
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.file).toBeDefined();
      expect(res.body.data.file.url).toMatch(/\/uploads\//);
    });

    it('should reject invalid MIME types (e.g. text/plain or .pdf) with 400 Bad Request', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload')
        .set('Authorization', `Bearer ${userToken}`)
        .attach('file', Buffer.from('malicious payload'), {
          filename: 'script.txt',
          contentType: 'text/plain',
        });

      expect(res.status).toBe(400);
      expect(res.body.success).toBe(false);
      expect(res.body.error.message).toMatch(/Invalid file format/i);
    });
  });

  describe('Get File Metadata (GET /api/v1/files/:id)', () => {
    it('should retrieve existing file metadata by UUID with 200 OK', async () => {
      const res = await request(app).get(`/api/v1/files/${mockFileRecord.id}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data.file.id).toBe(mockFileRecord.id);
    });

    it('should return 404 when file UUID does not exist', async () => {
      const res = await request(app).get('/api/v1/files/99999999-9999-4999-a999-999999999999');

      expect(res.status).toBe(404);
      expect(res.body.success).toBe(false);
      expect(res.body.error.code).toBe('NOT_FOUND');
    });
  });

  describe('Delete File (DELETE /api/v1/files/:id)', () => {
    it('should return 403 Forbidden when a non-owner/non-admin attempts deletion', async () => {
      const res = await request(app)
        .delete(`/api/v1/files/${mockFileRecord.id}`)
        .set('Authorization', `Bearer ${otherUserToken}`);

      expect(res.status).toBe(403);
      expect(res.body.success).toBe(false);
      expect(res.body.error.message).toMatch(/permission/i);
    });

    it('should allow file owner to delete their file with 200 OK', async () => {
      const res = await request(app)
        .delete(`/api/v1/files/${mockFileRecord.id}`)
        .set('Authorization', `Bearer ${userToken}`);

      expect(res.status).toBe(200);
      expect(res.body.success).toBe(true);
    });
  });
});
