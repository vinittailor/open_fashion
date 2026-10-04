import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/user_management_repository_impl.dart';
import '../../domain/models/admin_user_model.dart';
import '../../domain/repositories/user_management_repository.dart';

/// Immutable State for User Management
class UserManagementState {
  final bool isLoading;
  final bool isActionLoading;
  final String? errorMessage;
  final String? successMessage;
  final List<AdminUserModel> users;
  final PaginationModel pagination;
  final String searchQuery;
  final String? roleFilter;
  final String statusFilter; // 'all', 'active', 'deleted'
  final String sortBy;
  final String sortOrder;

  const UserManagementState({
    this.isLoading = false,
    this.isActionLoading = false,
    this.errorMessage,
    this.successMessage,
    this.users = const [],
    required this.pagination,
    this.searchQuery = '',
    this.roleFilter,
    this.statusFilter = 'all',
    this.sortBy = 'createdAt',
    this.sortOrder = 'desc',
  });

  factory UserManagementState.initial() {
    return UserManagementState(
      pagination: PaginationModel.initial(),
    );
  }

  UserManagementState copyWith({
    bool? isLoading,
    bool? isActionLoading,
    String? errorMessage,
    String? successMessage,
    List<AdminUserModel>? users,
    PaginationModel? pagination,
    String? searchQuery,
    String? roleFilter,
    String? statusFilter,
    String? sortBy,
    String? sortOrder,
    bool clearError = false,
    bool clearSuccess = false,
    bool clearRoleFilter = false,
  }) {
    return UserManagementState(
      isLoading: isLoading ?? this.isLoading,
      isActionLoading: isActionLoading ?? this.isActionLoading,
      errorMessage: clearError ? null : (errorMessage ?? this.errorMessage),
      successMessage: clearSuccess ? null : (successMessage ?? this.successMessage),
      users: users ?? this.users,
      pagination: pagination ?? this.pagination,
      searchQuery: searchQuery ?? this.searchQuery,
      roleFilter: clearRoleFilter ? null : (roleFilter ?? this.roleFilter),
      statusFilter: statusFilter ?? this.statusFilter,
      sortBy: sortBy ?? this.sortBy,
      sortOrder: sortOrder ?? this.sortOrder,
    );
  }
}

/// Riverpod StateNotifier managing User Management State
class UserManagementController extends StateNotifier<UserManagementState> {
  final UserManagementRepository _repository;

  UserManagementController(this._repository)
      : super(UserManagementState.initial()) {
    fetchUsers();
  }

  /// Fetches users using the current filter and pagination state
  Future<void> fetchUsers({int? page}) async {
    state = state.copyWith(isLoading: true, clearError: true, clearSuccess: true);

    try {
      final targetPage = page ?? state.pagination.currentPage;
      final response = await _repository.fetchUsers(
        page: targetPage,
        limit: state.pagination.limit,
        search: state.searchQuery,
        role: state.roleFilter,
        status: state.statusFilter,
        sortBy: state.sortBy,
        sortOrder: state.sortOrder,
      );

      state = state.copyWith(
        isLoading: false,
        users: response.users,
        pagination: response.pagination,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        errorMessage: e.toString().replaceAll('Exception: ', ''),
      );
    }
  }

  /// Updates live search query and triggers fetch
  void setSearchQuery(String query) {
    if (state.searchQuery == query) return;
    state = state.copyWith(searchQuery: query);
    fetchUsers(page: 1);
  }

  /// Updates role filter (ALL, ADMIN, CUSTOMER) and triggers fetch
  void setRoleFilter(String? role) {
    state = state.copyWith(
      roleFilter: (role == null || role == 'ALL') ? null : role,
      clearRoleFilter: role == null || role == 'ALL',
    );
    fetchUsers(page: 1);
  }

  /// Updates status filter ('all', 'active', 'deleted') and triggers fetch
  void setStatusFilter(String status) {
    state = state.copyWith(statusFilter: status);
    fetchUsers(page: 1);
  }

  /// Changes current page
  void changePage(int newPage) {
    if (newPage < 1 || newPage > state.pagination.totalPages) return;
    fetchUsers(page: newPage);
  }

  /// Updates a user's role
  Future<bool> updateUserRole(String userId, String newRole) async {
    state = state.copyWith(isActionLoading: true, clearError: true, clearSuccess: true);

    try {
      final updatedUser = await _repository.updateUserRole(userId, newRole);

      // Update in local state
      final updatedList = state.users.map((u) {
        return u.id == userId ? updatedUser : u;
      }).toList();

      state = state.copyWith(
        isActionLoading: false,
        users: updatedList,
        successMessage: 'Role updated to ${updatedUser.role} for ${updatedUser.name}',
      );
      return true;
    } catch (e) {
      state = state.copyWith(
        isActionLoading: false,
        errorMessage: e.toString().replaceAll('Exception: ', ''),
      );
      return false;
    }
  }

  /// Toggles user active / deactivated status
  Future<bool> toggleUserStatus(AdminUserModel user) async {
    state = state.copyWith(isActionLoading: true, clearError: true, clearSuccess: true);

    try {
      AdminUserModel updatedUser;
      if (user.isActive) {
        updatedUser = await _repository.deactivateUser(user.id);
      } else {
        updatedUser = await _repository.restoreUser(user.id);
      }

      final updatedList = state.users.map((u) {
        return u.id == user.id ? updatedUser : u;
      }).toList();

      state = state.copyWith(
        isActionLoading: false,
        users: updatedList,
        successMessage: updatedUser.isActive
            ? 'Account for ${user.name} has been restored'
            : 'Account for ${user.name} has been deactivated',
      );
      return true;
    } catch (e) {
      state = state.copyWith(
        isActionLoading: false,
        errorMessage: e.toString().replaceAll('Exception: ', ''),
      );
      return false;
    }
  }

  /// Clears notifications
  void clearMessages() {
    state = state.copyWith(clearError: true, clearSuccess: true);
  }
}

/// Provider for UserManagementController
final userManagementControllerProvider =
    StateNotifierProvider<UserManagementController, UserManagementState>((ref) {
  final repository = ref.watch(userManagementRepositoryProvider);
  return UserManagementController(repository);
});
