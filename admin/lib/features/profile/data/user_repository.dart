import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/api_client.dart';
import '../../auth/domain/models/user_model.dart';

/// User profile repository provider
final userRepositoryProvider = Provider<UserRepository>((ref) {
  final dio = ref.watch(dioProvider);
  return UserRepository(dio);
});

/// Data repository for authenticated user profile operations
class UserRepository {
  final Dio _dio;

  UserRepository(this._dio);

  /// Fetches the authenticated user profile via GET /users/me
  Future<UserModel> getMe() async {
    try {
      final response = await _dio.get('/users/me');
      final responseData = response.data as Map<String, dynamic>;
      final data = responseData['data'] as Map<String, dynamic>;
      final userJson = data['user'] as Map<String, dynamic>;

      return UserModel.fromJson(userJson);
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to fetch user profile.');
    }
  }

  /// Updates authenticated user details via PATCH /users/me
  Future<UserModel> updateMe({
    String? name,
    String? phoneNumber,
  }) async {
    try {
      final response = await _dio.patch(
        '/users/me',
        data: {
          if (name != null && name.trim().isNotEmpty) 'name': name.trim(),
          if (phoneNumber != null) 'phoneNumber': phoneNumber.trim(),
        },
      );

      final responseData = response.data as Map<String, dynamic>;
      final data = responseData['data'] as Map<String, dynamic>;
      final userJson = data['user'] as Map<String, dynamic>;

      return UserModel.fromJson(userJson);
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to update user profile.');
    }
  }

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
