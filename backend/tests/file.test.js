import { describe, it, expect, beforeAll, vi } from 'vitest';
import request from 'supertest';
import { app } from '../src/app.js';
import { signAccessToken } from '../src/utils/jwt.js';

const { mockFileRecord, mockProduct, mockProductImage } = vi.hoisted(() => {
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

  const product = {
    id: '44444444-4444-4444-a444-444444444444',
    title: 'Luxury Silk Dress',
    slug: 'luxury-silk-dress',
  };

  const productImage = {
    id: '55555555-5555-4555-a555-555555555555',
    productId: product.id,
    fileId: file.id,
    url: file.url,
    isPrimary: true,
    sortOrder: 0,
  };

  return { mockFileRecord: file, mockProduct: product, mockProductImage: productImage };
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
vi.mock('../src/config/prisma.js', () => {
  const fileOps = {
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
  };

  const productOps = {
    findUnique: vi.fn().mockImplementation(({ where }) => {
      if (where.id === mockProduct.id) {
        return Promise.resolve(mockProduct);
      }
      return Promise.resolve(null);
    }),
  };

  const productImageOps = {
    count: vi.fn().mockResolvedValue(0),
    create: vi.fn().mockImplementation(({ data }) => {
      return Promise.resolve({
        ...mockProductImage,
        ...data,
        id: mockProductImage.id,
      });
    }),
    updateMany: vi.fn().mockResolvedValue({ count: 1 }),
  };

  const tx = {
    file: fileOps,
    product: productOps,
    productImage: productImageOps,
  };

  return {
    prisma: {
      file: fileOps,
      product: productOps,
      productImage: productImageOps,
      $transaction: vi.fn().mockImplementation(async (callback) => {
        if (typeof callback === 'function') {
          return await callback(tx);
        }
        return Promise.all(callback);
      }),
      $on: vi.fn(),
    },
  };
});

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

  describe('Multi-File Batch Upload (POST /api/v1/files/upload-multiple) - Micro-Slice 2.2.1', () => {
    it('should return 401 when uploading multiple files without authentication', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload-multiple')
        .attach('files', Buffer.from('image1'), { filename: 'p1.jpg', contentType: 'image/jpeg' });

      expect(res.status).toBe(401);
      expect(res.body.success).toBe(false);
    });

    it('should upload a batch of files and return 201 with created file records', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload-multiple')
        .set('Authorization', `Bearer ${userToken}`)
        .attach('files', Buffer.from('img-1-data'), { filename: 'gallery1.webp', contentType: 'image/webp' })
        .attach('files', Buffer.from('img-2-data'), { filename: 'gallery2.webp', contentType: 'image/webp' });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(Array.isArray(res.body.data.files)).toBe(true);
      expect(res.body.data.files.length).toBe(2);
    });

    it('should upload multiple files and link them to a product when productId is provided', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload-multiple')
        .set('Authorization', `Bearer ${userToken}`)
        .field('productId', mockProduct.id)
        .attach('files', Buffer.from('product-img-1'), { filename: 'prod1.jpg', contentType: 'image/jpeg' })
        .attach('files', Buffer.from('product-img-2'), { filename: 'prod2.jpg', contentType: 'image/jpeg' });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.files).toBeDefined();
      expect(res.body.data.productImages).toBeDefined();
      expect(res.body.data.productImages.length).toBe(2);
      expect(res.body.data.productImages[0].isPrimary).toBe(true);
    });

    it('should return 404 when associating upload with non-existent productId', async () => {
      const res = await request(app)
        .post('/api/v1/files/upload-multiple')
        .set('Authorization', `Bearer ${userToken}`)
        .field('productId', '99999999-9999-4999-a999-999999999999')
        .attach('files', Buffer.from('fake-data'), { filename: 'prod.jpg', contentType: 'image/jpeg' });

      expect(res.status).toBe(404);
      expect(res.body.success).toBe(false);
    });
  });

  describe('Link Files to Product Gallery (POST /api/v1/files/link-product) - Micro-Slice 2.2.1', () => {
    it('should link existing files to a product and return 201 with productImages', async () => {
      const res = await request(app)
        .post('/api/v1/files/link-product')
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          productId: mockProduct.id,
          images: [
            { fileId: mockFileRecord.id, isPrimary: true, sortOrder: 0 },
          ],
        });

      expect(res.status).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.productImages).toBeDefined();
      expect(res.body.data.productImages[0].isPrimary).toBe(true);
    });

    it('should return 404 when linking to a non-existent product', async () => {
      const res = await request(app)
        .post('/api/v1/files/link-product')
        .set('Authorization', `Bearer ${userToken}`)
        .send({
          productId: '99999999-9999-4999-a999-999999999999',
          images: [{ fileId: mockFileRecord.id }],
        });

      expect(res.status).toBe(404);
      expect(res.body.success).toBe(false);
    });
  });
});

