---
name: zod-v4-standards
description: Enforces modern Zod 4 syntax standards, top-level schema validators, and pipeline patterns across the Open Fashion backend.
---

# Zod 4 Standards & Modern Conventions

## 1. Core Syntax Shifts from Zod 3 to Zod 4

### 1.1 Custom Error Messages
- ❌ **Deprecated**: `{ message: '...' }`, `{ required_error: '...' }`, `{ invalid_type_error: '...' }`
- ✅ **Standard (Zod 4)**: `{ error: 'Custom error message' }`

```javascript
// Correct Zod 4
const nameSchema = z.string({ error: 'Name is required' });
```

### 1.2 Top-Level String Format Schemas
In Zod 4, string format methods chained directly on `z.string()` (like `.email()`, `.uuid()`, `.url()`) are deprecated. Formats are now top-level schemas:

- ❌ **Deprecated**: `z.string().email()`, `z.string().uuid()`, `z.string().url()`
- ✅ **Standard (Zod 4)**: `z.email()`, `z.uuid()`, `z.url()`, `z.e164()`, `z.jwt()`

### 1.3 Combining Transformations and Formats with `.pipe()`
When transforming an input (e.g. trimming or lowercasing) before validating its format, chain them using `.pipe()`:

```javascript
// Clean, Trim, Lowercase, then Validate Email
const emailSchema = z
  .string({ error: 'Email is required' })
  .trim()
  .toLowerCase()
  .pipe(z.email({ error: 'Please provide a valid email address' }));

// Clean, Trim, then Validate E.164 Phone Number
const phoneSchema = z
  .string({ error: 'Phone must be a string' })
  .trim()
  .pipe(z.e164({ error: 'Please provide a valid E.164 phone number' }))
  .optional();
```

### 1.4 Base Schema Types
- ❌ **Deprecated**: `ZodSchema`, `ZodTypeAny`
- ✅ **Standard (Zod 4)**: `ZodType` (imported via `import { ZodType } from 'zod'` or `import('zod').ZodType`)
