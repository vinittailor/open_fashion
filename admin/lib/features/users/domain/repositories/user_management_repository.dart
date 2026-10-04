import '../models/admin_user_model.dart';

/// Abstract Domain Contract for Admin User Management Operations
abstract class UserManagementRepository {
  /// Fetches a paginated, filtered list of users
  Future<AdminUserListResponse> fetchUsers({
    int page = 1,
    int limit = 10,
    String? search,
    String? role,
    String status = 'all',
    String sortBy = 'createdAt',
    String sortOrder = 'desc',
  });

  /// Fetches detailed information for a single user by ID
  Future<AdminUserModel> fetchUserById(String userId);

  /// Updates a user's role (ADMIN / CUSTOMER)
  Future<AdminUserModel> updateUserRole(String userId, String newRole);

  /// Soft deletes / deactivates a user account
  Future<AdminUserModel> deactivateUser(String userId);

  /// Restores a soft-deleted user account
  Future<AdminUserModel> restoreUser(String userId);
}
