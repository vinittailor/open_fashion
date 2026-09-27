/**
 * Higher-order middleware function that validates incoming request data against a Zod schema.
 *
 * @param {import('zod').ZodType} schema - The Zod schema to validate against (can encompass body, query, and params).
 * @returns {import('express').RequestHandler} Express middleware handler.
 */
export const validate = (schema) => async (req, res, next) => {
  try {
    const validatedData = await schema.parseAsync({
      body: req.body,
      query: req.query,
      params: req.params,
    });

    // Replace request parts with sanitized and transformed values
    if (validatedData.body !== undefined) {
      req.body = validatedData.body;
    }
    if (validatedData.query !== undefined) {
      req.query = validatedData.query;
    }
    if (validatedData.params !== undefined) {
      req.params = validatedData.params;
    }

    next();
  } catch (error) {
    // Forward ZodError directly to global errorHandler
    next(error);
  }
};
