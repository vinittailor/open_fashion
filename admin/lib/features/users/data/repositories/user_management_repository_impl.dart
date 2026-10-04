import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/network/api_client.dart';
import '../../domain/models/admin_user_model.dart';
import '../../domain/repositories/user_management_repository.dart';

/// Provider for UserManagementRepository
final userManagementRepositoryProvider = Provider<UserManagementRepository>((ref) {
  final dio = ref.watch(dioProvider);
  return UserManagementRepositoryImpl(dio);
});

/// Concrete implementation of UserManagementRepository backed by Dio
class UserManagementRepositoryImpl implements UserManagementRepository {
  final Dio _dio;

  UserManagementRepositoryImpl(this._dio);

  @override
  Future<AdminUserListResponse> fetchUsers({
    int page = 1,
    int limit = 10,
    String? search,
    String? role,
    String status = 'all',
    String sortBy = 'createdAt',
    String sortOrder = 'desc',
  }) async {
    try {
      final queryParams = <String, dynamic>{
        'page': page,
        'limit': limit,
        'status': status,
        'sortBy': sortBy,
        'sortOrder': sortOrder,
      };

      if (search != null && search.trim().isNotEmpty) {
        queryParams['search'] = search.trim();
      }

      if (role != null && role.isNotEmpty && role != 'ALL') {
        queryParams['role'] = role;
      }

      final response = await _dio.get(
        '/admin/users',
        queryParameters: queryParams,
      );

      final responseData = response.data['data'] as Map<String, dynamic>;
      return AdminUserListResponse.fromJson(responseData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to load users';
      throw Exception(message);
    }
  }

  @override
  Future<AdminUserModel> fetchUserById(String userId) async {
    try {
      final response = await _dio.get('/admin/users/$userId');
      final userData = response.data['data']['user'] as Map<String, dynamic>;
      return AdminUserModel.fromJson(userData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to fetch user details';
      throw Exception(message);
    }
  }

  @override
  Future<AdminUserModel> updateUserRole(String userId, String newRole) async {
    try {
      final response = await _dio.patch(
        '/admin/users/$userId/role',
        data: {'role': newRole},
      );
      final userData = response.data['data']['user'] as Map<String, dynamic>;
      return AdminUserModel.fromJson(userData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to update user role';
      throw Exception(message);
    }
  }

  @override
  Future<AdminUserModel> deactivateUser(String userId) async {
    try {
      final response = await _dio.delete('/admin/users/$userId');
      final userData = response.data['data']['user'] as Map<String, dynamic>;
      return AdminUserModel.fromJson(userData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to deactivate user';
      throw Exception(message);
    }
  }

  @override
  Future<AdminUserModel> restoreUser(String userId) async {
    try {
      final response = await _dio.patch('/admin/users/$userId/restore');
      final userData = response.data['data']['user'] as Map<String, dynamic>;
      return AdminUserModel.fromJson(userData);
    } on DioException catch (e) {
      final message = e.response?.data?['error']?['message'] ??
          e.response?.data?['message'] ??
          'Failed to restore user';
      throw Exception(message);
    }
  }
}
