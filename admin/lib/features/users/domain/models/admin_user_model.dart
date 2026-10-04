/// Domain Model representing a managed user in the Admin Dashboard
class AdminUserModel {
  final String id;
  final String name;
  final String email;
  final String role;
  final String? phoneNumber;
  final bool isEmailVerified;
  final DateTime? deletedAt;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int ordersCount;
  final int reviewsCount;

  const AdminUserModel({
    required this.id,
    required this.name,
    required this.email,
    required this.role,
    this.phoneNumber,
    this.isEmailVerified = false,
    this.deletedAt,
    required this.createdAt,
    required this.updatedAt,
    this.ordersCount = 0,
    this.reviewsCount = 0,
  });

  bool get isActive => deletedAt == null;
  bool get isAdmin => role.toUpperCase() == 'ADMIN';

  factory AdminUserModel.fromJson(Map<String, dynamic> json) {
    final countData = json['_count'] as Map<String, dynamic>? ?? {};

    return AdminUserModel(
      id: json['id'] as String? ?? '',
      name: json['name'] as String? ?? 'Unnamed User',
      email: json['email'] as String? ?? '',
      role: json['role'] as String? ?? 'CUSTOMER',
      phoneNumber: json['phoneNumber'] as String?,
      isEmailVerified: json['isEmailVerified'] as bool? ?? false,
      deletedAt: json['deletedAt'] != null
          ? DateTime.tryParse(json['deletedAt'] as String)
          : null,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'] as String) ?? DateTime.now()
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String) ?? DateTime.now()
          : DateTime.now(),
      ordersCount: countData['orders'] as int? ?? 0,
      reviewsCount: countData['reviews'] as int? ?? 0,
    );
  }

  AdminUserModel copyWith({
    String? id,
    String? name,
    String? email,
    String? role,
    String? phoneNumber,
    bool? isEmailVerified,
    DateTime? deletedAt,
    DateTime? createdAt,
    DateTime? updatedAt,
    int? ordersCount,
    int? reviewsCount,
    bool clearDeletedAt = false,
  }) {
    return AdminUserModel(
      id: id ?? this.id,
      name: name ?? this.name,
      email: email ?? this.email,
      role: role ?? this.role,
      phoneNumber: phoneNumber ?? this.phoneNumber,
      isEmailVerified: isEmailVerified ?? this.isEmailVerified,
      deletedAt: clearDeletedAt ? null : (deletedAt ?? this.deletedAt),
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      ordersCount: ordersCount ?? this.ordersCount,
      reviewsCount: reviewsCount ?? this.reviewsCount,
    );
  }
}

/// Domain Model for Admin Pagination state
class PaginationModel {
  final int totalUsers;
  final int totalPages;
  final int currentPage;
  final int limit;
  final bool hasNextPage;
  final bool hasPrevPage;

  const PaginationModel({
    required this.totalUsers,
    required this.totalPages,
    required this.currentPage,
    required this.limit,
    required this.hasNextPage,
    required this.hasPrevPage,
  });

  factory PaginationModel.initial() {
    return const PaginationModel(
      totalUsers: 0,
      totalPages: 1,
      currentPage: 1,
      limit: 10,
      hasNextPage: false,
      hasPrevPage: false,
    );
  }

  factory PaginationModel.fromJson(Map<String, dynamic> json) {
    return PaginationModel(
      totalUsers: json['totalUsers'] as int? ?? 0,
      totalPages: json['totalPages'] as int? ?? 1,
      currentPage: json['currentPage'] as int? ?? 1,
      limit: json['limit'] as int? ?? 10,
      hasNextPage: json['hasNextPage'] as bool? ?? false,
      hasPrevPage: json['hasPrevPage'] as bool? ?? false,
    );
  }
}

/// Paginated Users response entity
class AdminUserListResponse {
  final List<AdminUserModel> users;
  final PaginationModel pagination;

  const AdminUserListResponse({
    required this.users,
    required this.pagination,
  });

  factory AdminUserListResponse.fromJson(Map<String, dynamic> json) {
    final usersList = (json['users'] as List<dynamic>?)
            ?.map((e) => AdminUserModel.fromJson(e as Map<String, dynamic>))
            .toList() ??
        [];

    final paginationData = json['pagination'] as Map<String, dynamic>? ?? {};

    return AdminUserListResponse(
      users: usersList,
      pagination: PaginationModel.fromJson(paginationData),
    );
  }
}
