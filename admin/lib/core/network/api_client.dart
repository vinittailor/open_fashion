import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../storage/token_storage.dart';

/// Base API URL configuration provider
final apiBaseUrlProvider = Provider<String>((ref) {
  // Default to localhost for Web/Desktop.
  return 'http://localhost:5000/api/v1';
});

/// Singleton Dio HTTP Client Provider configured with timeouts, auth interceptor, and logger
final dioProvider = Provider<Dio>((ref) {
  final baseUrl = ref.watch(apiBaseUrlProvider);
  final tokenStorage = ref.watch(tokenStorageProvider);

  final dio = Dio(
    BaseOptions(
      baseUrl: baseUrl,
      connectTimeout: const Duration(seconds: 10),
      receiveTimeout: const Duration(seconds: 10),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ),
  );

  // 1. JWT Bearer Token Injection Interceptor
  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        // Skip Authorization header on public auth endpoints
        final path = options.path;
        final isAuthEndpoint = path.contains('/auth/login') ||
            path.contains('/auth/register') ||
            path.contains('/auth/refresh');

        if (!isAuthEndpoint) {
          final token = await tokenStorage.getAccessToken();
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
        }
        return handler.next(options);
      },
    ),
  );

  // 2. Logging Interceptor
  dio.interceptors.add(
    LogInterceptor(
      requestBody: true,
      responseBody: true,
      error: true,
    ),
  );

  return dio;
});
