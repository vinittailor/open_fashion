import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/api_client.dart';
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
      if (e.response != null && e.response?.data is Map<String, dynamic>) {
        final errorPayload = e.response!.data as Map<String, dynamic>;
        final errorObj = errorPayload['error'] as Map<String, dynamic>?;
        final message = errorObj?['message'] as String? ??
            errorPayload['message'] as String? ??
            'Registration failed. Please check your inputs.';
        throw Exception(message);
      }
      throw Exception('Network error: ${e.message}');
    }
  }
}
