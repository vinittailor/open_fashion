import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/api_client.dart';
import '../domain/models/auth_response_model.dart';
import '../domain/models/user_model.dart';

/// Authentication Repository Provider
final authRepositoryProvider = Provider<AuthRepository>((ref) {
  final dio = ref.watch(dioProvider);
  return AuthRepository(dio);
});

/// Data Repository for Authentication Endpoints
class AuthRepository {
  final Dio _dio;

  AuthRepository(this._dio);

  /// Registers a new user via POST /auth/register
  Future<UserModel> register({
    required String name,
    required String email,
    required String password,
    String? phoneNumber,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/register',
        data: {
          'name': name,
          'email': email,
          'password': password,
          if (phoneNumber != null && phoneNumber.trim().isNotEmpty)
            'phoneNumber': phoneNumber.trim(),
        },
      );

      final responseData = response.data as Map<String, dynamic>;
      final data = responseData['data'] as Map<String, dynamic>;
      final userJson = (data['user'] ?? data) as Map<String, dynamic>;

      return UserModel.fromJson(userJson);
    } on DioException catch (e) {
      throw _handleDioError(e, 'Registration failed.');
    }
  }

  /// Authenticates user credentials via POST /auth/login
  Future<AuthResponseModel> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/login',
        data: {
          'email': email,
          'password': password,
        },
      );

      final responseData = response.data as Map<String, dynamic>;
      final data = responseData['data'] as Map<String, dynamic>;

      return AuthResponseModel.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e, 'Login failed. Please check your credentials.');
    }
  }

  /// Rotates access token via POST /auth/refresh
  Future<AuthResponseModel> refreshToken({
    required String refreshToken,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/refresh',
        data: {
          'refreshToken': refreshToken,
        },
      );

      final responseData = response.data as Map<String, dynamic>;
      final data = responseData['data'] as Map<String, dynamic>;

      return AuthResponseModel.fromJson(data);
    } on DioException catch (e) {
      throw _handleDioError(e, 'Session expired. Please sign in again.');
    }
  }

  /// Revokes active session via POST /auth/logout
  Future<void> logout({String? userId}) async {
    try {
      await _dio.post(
        '/auth/logout',
        data: {
          if (userId != null) 'userId': userId,
        },
      );
    } catch (_) {
      // Best-effort logout: ignore network failures
    }
  }

  /// Extracts readable error messages from Dio responses
  Exception _handleDioError(DioException e, String defaultMessage) {
    if (e.response != null && e.response?.data is Map<String, dynamic>) {
      final errorPayload = e.response!.data as Map<String, dynamic>;
      final errorObj = errorPayload['error'] as Map<String, dynamic>?;
      final message = errorObj?['message'] as String? ??
          errorPayload['message'] as String? ??
          defaultMessage;
      return Exception(message);
    }
    return Exception(e.message ?? defaultMessage);
  }
}
