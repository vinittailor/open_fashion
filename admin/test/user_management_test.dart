import 'package:flutter_test/flutter_test.dart';
import 'package:open_fashion_admin/features/users/domain/models/admin_user_model.dart';
import 'package:open_fashion_admin/features/users/domain/repositories/user_management_repository.dart';
import 'package:open_fashion_admin/features/users/presentation/controllers/user_management_controller.dart';

class MockUserManagementRepository implements UserManagementRepository {
  final List<AdminUserModel> _mockUsers = [
    AdminUserModel(
      id: '11111111-1111-4111-a111-111111111111',
      name: 'Admin User',
      email: 'admin@openfashion.com',
      role: 'ADMIN',
      phoneNumber: '+1987654321',
      isEmailVerified: true,
      deletedAt: null,
      createdAt: DateTime(2026, 1, 1),
      updatedAt: DateTime(2026, 1, 1),
      ordersCount: 5,
      reviewsCount: 2,
    ),
    AdminUserModel(
      id: '22222222-2222-4222-a222-222222222222',
      name: 'Jane Customer',
      email: 'jane@example.com',
      role: 'CUSTOMER',
      phoneNumber: '+1234567890',
      isEmailVerified: true,
      deletedAt: null,
      createdAt: DateTime(2026, 2, 1),
      updatedAt: DateTime(2026, 2, 1),
      ordersCount: 1,
      reviewsCount: 0,
    ),
  ];

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
    var filtered = List<AdminUserModel>.from(_mockUsers);

    if (search != null && search.isNotEmpty) {
      filtered = filtered
          .where((u) =>
              u.name.toLowerCase().contains(search.toLowerCase()) ||
              u.email.toLowerCase().contains(search.toLowerCase()))
          .toList();
    }

    if (role != null && role.isNotEmpty) {
      filtered = filtered.where((u) => u.role == role).toList();
    }

    return AdminUserListResponse(
      users: filtered,
      pagination: PaginationModel(
        totalUsers: filtered.length,
        totalPages: 1,
        currentPage: page,
        limit: limit,
        hasNextPage: false,
        hasPrevPage: false,
      ),
    );
  }

  @override
  Future<AdminUserModel> fetchUserById(String userId) async {
    return _mockUsers.firstWhere((u) => u.id == userId);
  }

  @override
  Future<AdminUserModel> updateUserRole(String userId, String newRole) async {
    final index = _mockUsers.indexWhere((u) => u.id == userId);
    final updated = _mockUsers[index].copyWith(role: newRole);
    _mockUsers[index] = updated;
    return updated;
  }

  @override
  Future<AdminUserModel> deactivateUser(String userId) async {
    final index = _mockUsers.indexWhere((u) => u.id == userId);
    final updated = _mockUsers[index].copyWith(deletedAt: DateTime.now());
    _mockUsers[index] = updated;
    return updated;
  }

  @override
  Future<AdminUserModel> restoreUser(String userId) async {
    final index = _mockUsers.indexWhere((u) => u.id == userId);
    final updated = _mockUsers[index].copyWith(clearDeletedAt: true);
    _mockUsers[index] = updated;
    return updated;
  }
}

void main() {
  group('UserManagementController Unit Tests', () {
    late MockUserManagementRepository mockRepo;
    late UserManagementController controller;

    setUp(() {
      mockRepo = MockUserManagementRepository();
      controller = UserManagementController(mockRepo);
    });

    test('Initial fetch populates users and pagination metadata', () async {
      await controller.fetchUsers();

      expect(controller.state.isLoading, false);
      expect(controller.state.users.length, 2);
      expect(controller.state.pagination.totalUsers, 2);
      expect(controller.state.users.first.name, 'Admin User');
    });

    test('Search filter restricts returned users', () async {
      controller.setSearchQuery('Jane');
      await controller.fetchUsers();

      expect(controller.state.users.length, 1);
      expect(controller.state.users.first.name, 'Jane Customer');
    });

    test('Role filter restricts returned users to ADMIN only', () async {
      controller.setRoleFilter('ADMIN');
      await controller.fetchUsers();

      expect(controller.state.users.length, 1);
      expect(controller.state.users.first.role, 'ADMIN');
    });

    test('Role update changes user role in state', () async {
      final success = await controller.updateUserRole(
        '22222222-2222-4222-a222-222222222222',
        'ADMIN',
      );

      expect(success, true);
      final updatedJane = controller.state.users.firstWhere(
        (u) => u.id == '22222222-2222-4222-a222-222222222222',
      );
      expect(updatedJane.role, 'ADMIN');
    });

    test('Toggle status deactivates and restores active state', () async {
      final jane = controller.state.users.firstWhere(
        (u) => u.id == '22222222-2222-4222-a222-222222222222',
      );

      // Deactivate
      final deactivateSuccess = await controller.toggleUserStatus(jane);
      expect(deactivateSuccess, true);

      final deactivatedJane = controller.state.users.firstWhere(
        (u) => u.id == '22222222-2222-4222-a222-222222222222',
      );
      expect(deactivatedJane.isActive, false);

      // Restore
      final restoreSuccess = await controller.toggleUserStatus(deactivatedJane);
      expect(restoreSuccess, true);

      final restoredJane = controller.state.users.firstWhere(
        (u) => u.id == '22222222-2222-4222-a222-222222222222',
      );
      expect(restoredJane.isActive, true);
    });
  });
}
