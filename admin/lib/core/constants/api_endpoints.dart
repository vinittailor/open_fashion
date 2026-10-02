/// Centralized HTTP API Endpoint Paths for Open Fashion Admin
abstract class ApiEndpoints {
  // --- Base Prefix ---
  static const String apiV1 = '/api/v1';

  // --- Authentication Endpoints ---
  static const String register = '/auth/register';
  static const String login = '/auth/login';
  static const String refresh = '/auth/refresh';
  static const String logout = '/auth/logout';
  static const String forgotPassword = '/auth/forgot-password';
  static const String resetPassword = '/auth/reset-password';
  static const String sendVerification = '/auth/send-verification';
  static const String verifyEmail = '/auth/verify-email';

  // --- User Profile & Account Endpoints ---
  static const String userMe = '/users/me';

  // --- Admin User Management Endpoints ---
  static const String adminUsers = '/admin/users';

  // --- Product Catalog Endpoints ---
  static const String products = '/products';
  static const String categories = '/categories';

  // --- Orders & Fulfillment Endpoints ---
  static const String orders = '/orders';
  static const String orderAnalytics = '/orders/analytics';
}
