import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/constants/breakpoints.dart';
import '../../../../core/theme/app_colors.dart';
import '../../../../core/theme/app_typography.dart';
import '../../domain/models/admin_user_model.dart';
import '../controllers/user_management_controller.dart';
import '../../../auth/presentation/screens/register_screen.dart';

/// Screen displaying the interactive User Management Data Table
class UserManagementScreen extends ConsumerStatefulWidget {
  const UserManagementScreen({super.key});

  @override
  ConsumerState<UserManagementScreen> createState() => _UserManagementScreenState();
}

class _UserManagementScreenState extends ConsumerState<UserManagementScreen> {
  final TextEditingController _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final state = ref.watch(userManagementControllerProvider);
    final controller = ref.read(userManagementControllerProvider.notifier);

    // Listen for toasts/snackbars
    ref.listen<UserManagementState>(userManagementControllerProvider, (prev, next) {
      if (next.errorMessage != null && next.errorMessage != prev?.errorMessage) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.errorMessage!),
            backgroundColor: AppColors.error,
          ),
        );
      }
      if (next.successMessage != null && next.successMessage != prev?.successMessage) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(next.successMessage!),
            backgroundColor: AppColors.success,
          ),
        );
      }
    });

    return Scaffold(
      backgroundColor: isDark ? AppColors.backgroundDark : AppColors.backgroundLight,
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 1. Header & Quick Actions Bar
            _buildHeader(context, isDark),
            const SizedBox(height: 20),

            // 2. Filter & Live Search Toolbar
            _buildFilterToolbar(context, isDark, state, controller),
            const SizedBox(height: 20),

            // 3. Main Data Content (Table for Desktop, Cards for Mobile)
            Expanded(
              child: state.isLoading
                  ? const Center(child: CircularProgressIndicator(color: AppColors.accent))
                  : state.users.isEmpty
                      ? _buildEmptyState(isDark)
                      : Breakpoints.isDesktop(context)
                          ? _buildDesktopDataTable(context, isDark, state, controller)
                          : _buildMobileUserList(context, isDark, state, controller),
            ),

            const SizedBox(height: 16),

            // 4. Pagination Controls Footer
            if (!state.isLoading && state.users.isNotEmpty)
              _buildPaginationFooter(isDark, state, controller),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context, bool isDark) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'CUSTOMER & USER DIRECTORY',
              style: AppTypography.displayMedium(isDark),
            ),
            const SizedBox(height: 4),
            Text(
              'Manage registered accounts, role privileges, and active statuses.',
              style: AppTypography.bodySmall(isDark),
            ),
          ],
        ),
        ElevatedButton.icon(
          style: ElevatedButton.styleFrom(
            backgroundColor: AppColors.accent,
            foregroundColor: Colors.black,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          ),
          icon: const Icon(Icons.person_add_alt_1, size: 18),
          label: const Text('INVITE USER', style: TextStyle(fontWeight: FontWeight.bold)),
          onPressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(builder: (_) => const RegisterScreen()),
            );
          },
        ),
      ],
    );
  }

  Widget _buildFilterToolbar(
    BuildContext context,
    bool isDark,
    UserManagementState state,
    UserManagementController controller,
  ) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isDark ? AppColors.borderDark : AppColors.borderLight,
        ),
      ),
      child: Wrap(
        spacing: 16,
        runSpacing: 12,
        alignment: WrapAlignment.spaceBetween,
        crossAxisAlignment: WrapCrossAlignment.center,
        children: [
          // Search Input
          SizedBox(
            width: 280,
            child: TextField(
              controller: _searchController,
              onChanged: (val) => controller.setSearchQuery(val),
              style: AppTypography.bodyMedium(isDark),
              decoration: InputDecoration(
                hintText: 'Search by name or email...',
                hintStyle: AppTypography.bodySmall(isDark),
                prefixIcon: const Icon(Icons.search, size: 20),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear, size: 18),
                        onPressed: () {
                          _searchController.clear();
                          controller.setSearchQuery('');
                        },
                      )
                    : null,
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: BorderSide(
                    color: isDark ? AppColors.borderDark : AppColors.borderLight,
                  ),
                ),
              ),
            ),
          ),

          // Role Filter Dropdown
          DropdownButtonHideUnderline(
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                border: Border.all(
                  color: isDark ? AppColors.borderDark : AppColors.borderLight,
                ),
              ),
              child: DropdownButton<String>(
                value: state.roleFilter ?? 'ALL',
                dropdownColor: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
                style: AppTypography.labelMedium(isDark),
                items: const [
                  DropdownMenuItem(value: 'ALL', child: Text('All Roles')),
                  DropdownMenuItem(value: 'CUSTOMER', child: Text('Customer')),
                  DropdownMenuItem(value: 'ADMIN', child: Text('Admin')),
                ],
                onChanged: (role) => controller.setRoleFilter(role),
              ),
            ),
          ),

          // Status Filter Segmented Controls
          SegmentedButton<String>(
            segments: const [
              ButtonSegment(value: 'all', label: Text('All')),
              ButtonSegment(value: 'active', label: Text('Active')),
              ButtonSegment(value: 'deleted', label: Text('Deactivated')),
            ],
            selected: {state.statusFilter},
            onSelectionChanged: (set) => controller.setStatusFilter(set.first),
            style: ButtonStyle(
              visualDensity: VisualDensity.compact,
            ),
          ),

          // Refresh Button
          IconButton(
            icon: const Icon(Icons.refresh),
            tooltip: 'Refresh user list',
            onPressed: () => controller.fetchUsers(),
          ),
        ],
      ),
    );
  }

  Widget _buildDesktopDataTable(
    BuildContext context,
    bool isDark,
    UserManagementState state,
    UserManagementController controller,
  ) {
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isDark ? AppColors.borderDark : AppColors.borderLight,
        ),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: SingleChildScrollView(
          child: DataTable(
            headingRowColor: WidgetStateProperty.all(
              isDark
                  ? AppColors.cardDark
                  : AppColors.backgroundLight,
            ),
            columns: [
              DataColumn(label: Text('USER', style: AppTypography.tableHeader)),
              DataColumn(label: Text('ROLE', style: AppTypography.tableHeader)),
              DataColumn(label: Text('EMAIL STATUS', style: AppTypography.tableHeader)),
              DataColumn(label: Text('ORDERS', style: AppTypography.tableHeader)),
              DataColumn(label: Text('JOINED DATE', style: AppTypography.tableHeader)),
              DataColumn(label: Text('STATUS', style: AppTypography.tableHeader)),
              DataColumn(label: Text('ACTIONS', style: AppTypography.tableHeader)),
            ],
            rows: state.users.map((user) {
              return DataRow(
                cells: [
                  // User info
                  DataCell(
                    Row(
                      children: [
                        CircleAvatar(
                          radius: 16,
                          backgroundColor: AppColors.accent.withValues(alpha: 0.2),
                          child: Text(
                            user.name.isNotEmpty ? user.name[0].toUpperCase() : 'U',
                            style: const TextStyle(
                              color: AppColors.accent,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(user.name, style: AppTypography.labelLarge(isDark)),
                            Text(user.email, style: AppTypography.bodySmall(isDark)),
                          ],
                        ),
                      ],
                    ),
                  ),

                  // Role
                  DataCell(
                    _buildRoleBadge(user.role, isDark),
                  ),

                  // Email Status
                  DataCell(
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          user.isEmailVerified ? Icons.verified : Icons.error_outline,
                          size: 16,
                          color: user.isEmailVerified ? AppColors.success : AppColors.warning,
                        ),
                        const SizedBox(width: 6),
                        Text(
                          user.isEmailVerified ? 'Verified' : 'Unverified',
                          style: TextStyle(
                            fontSize: 12,
                            color: user.isEmailVerified ? AppColors.success : AppColors.warning,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),

                  // Orders count
                  DataCell(
                    Text('${user.ordersCount} orders', style: AppTypography.bodySmall(isDark)),
                  ),

                  // Joined date
                  DataCell(
                    Text(
                      '${user.createdAt.year}-${user.createdAt.month.toString().padLeft(2, '0')}-${user.createdAt.day.toString().padLeft(2, '0')}',
                      style: AppTypography.bodySmall(isDark),
                    ),
                  ),

                  // Active status
                  DataCell(
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: user.isActive
                            ? AppColors.success.withValues(alpha: 0.12)
                            : AppColors.error.withValues(alpha: 0.12),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        user.isActive ? 'ACTIVE' : 'DEACTIVATED',
                        style: TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                          color: user.isActive ? AppColors.success : AppColors.error,
                        ),
                      ),
                    ),
                  ),

                  // Actions
                  DataCell(
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // Edit Role Button
                        IconButton(
                          icon: const Icon(Icons.admin_panel_settings_outlined, size: 20),
                          tooltip: 'Change User Role',
                          onPressed: () => _showChangeRoleDialog(context, user, controller),
                        ),
                        // Toggle Active / Deactivate Button
                        IconButton(
                          icon: Icon(
                            user.isActive ? Icons.block_outlined : Icons.check_circle_outline,
                            size: 20,
                            color: user.isActive ? AppColors.error : AppColors.success,
                          ),
                          tooltip: user.isActive ? 'Deactivate User' : 'Restore User',
                          onPressed: () => _showStatusConfirmDialog(context, user, controller),
                        ),
                      ],
                    ),
                  ),
                ],
              );
            }).toList(),
          ),
        ),
      ),
    );
  }

  Widget _buildMobileUserList(
    BuildContext context,
    bool isDark,
    UserManagementState state,
    UserManagementController controller,
  ) {
    return ListView.separated(
      itemCount: state.users.length,
      separatorBuilder: (_, __) => const SizedBox(height: 12),
      itemBuilder: (context, index) {
        final user = state.users[index];
        return Card(
          color: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
            side: BorderSide(
              color: isDark ? AppColors.borderDark : AppColors.borderLight,
            ),
          ),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    CircleAvatar(
                      radius: 18,
                      backgroundColor: AppColors.accent.withValues(alpha: 0.2),
                      child: Text(
                        user.name.isNotEmpty ? user.name[0].toUpperCase() : 'U',
                        style: const TextStyle(
                          color: AppColors.accent,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(user.name, style: AppTypography.labelLarge(isDark)),
                          Text(user.email, style: AppTypography.bodySmall(isDark)),
                        ],
                      ),
                    ),
                    _buildRoleBadge(user.role, isDark),
                  ],
                ),
                const Divider(height: 24),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      user.isActive ? 'Status: Active' : 'Status: Deactivated',
                      style: TextStyle(
                        color: user.isActive ? AppColors.success : AppColors.error,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Row(
                      children: [
                        IconButton(
                          icon: const Icon(Icons.admin_panel_settings_outlined, size: 20),
                          onPressed: () => _showChangeRoleDialog(context, user, controller),
                        ),
                        IconButton(
                          icon: Icon(
                            user.isActive ? Icons.block_outlined : Icons.check_circle_outline,
                            size: 20,
                            color: user.isActive ? AppColors.error : AppColors.success,
                          ),
                          onPressed: () => _showStatusConfirmDialog(context, user, controller),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildRoleBadge(String role, bool isDark) {
    final isAdmin = role.toUpperCase() == 'ADMIN';
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: isAdmin
            ? AppColors.accent.withValues(alpha: 0.15)
            : (isDark ? AppColors.cardDark : AppColors.backgroundLight),
        borderRadius: BorderRadius.circular(6),
        border: Border.all(
          color: isAdmin ? AppColors.accent : (isDark ? AppColors.borderDark : AppColors.borderLight),
        ),
      ),
      child: Text(
        role.toUpperCase(),
        style: TextStyle(
          fontSize: 11,
          fontWeight: FontWeight.bold,
          color: isAdmin ? AppColors.accent : (isDark ? AppColors.textSecondaryDark : AppColors.textSecondaryLight),
        ),
      ),
    );
  }

  Widget _buildPaginationFooter(
    bool isDark,
    UserManagementState state,
    UserManagementController controller,
  ) {
    final pagination = state.pagination;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDark : AppColors.surfaceLight,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isDark ? AppColors.borderDark : AppColors.borderLight,
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            'Total: ${pagination.totalUsers} registered users · Page ${pagination.currentPage} of ${pagination.totalPages}',
            style: AppTypography.bodySmall(isDark),
          ),
          Row(
            children: [
              OutlinedButton.icon(
                icon: const Icon(Icons.chevron_left, size: 16),
                label: const Text('Previous'),
                onPressed: pagination.hasPrevPage
                    ? () => controller.changePage(pagination.currentPage - 1)
                    : null,
              ),
              const SizedBox(width: 8),
              OutlinedButton.icon(
                icon: const Icon(Icons.chevron_right, size: 16),
                label: const Text('Next'),
                onPressed: pagination.hasNextPage
                    ? () => controller.changePage(pagination.currentPage + 1)
                    : null,
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState(bool isDark) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.people_outline, size: 64, color: AppColors.textSecondaryLight),
          const SizedBox(height: 16),
          Text('No Users Found', style: AppTypography.titleLarge(isDark)),
          const SizedBox(height: 8),
          Text(
            'Try adjusting your search query or role filter.',
            style: AppTypography.bodySmall(isDark),
          ),
        ],
      ),
    );
  }

  void _showChangeRoleDialog(
    BuildContext context,
    AdminUserModel user,
    UserManagementController controller,
  ) {
    final newRole = user.isAdmin ? 'CUSTOMER' : 'ADMIN';
    showDialog(
      context: context,
      builder: (dialogCtx) => AlertDialog(
        title: const Text('Change User Role'),
        content: Text(
          'Are you sure you want to change ${user.name}\'s role from ${user.role} to $newRole?',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(dialogCtx).pop(),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.accent),
            onPressed: () {
              Navigator.of(dialogCtx).pop();
              controller.updateUserRole(user.id, newRole);
            },
            child: const Text('Confirm Change', style: TextStyle(color: Colors.black)),
          ),
        ],
      ),
    );
  }

  void _showStatusConfirmDialog(
    BuildContext context,
    AdminUserModel user,
    UserManagementController controller,
  ) {
    final actionName = user.isActive ? 'Deactivate' : 'Restore';
    showDialog(
      context: context,
      builder: (dialogCtx) => AlertDialog(
        title: Text('$actionName Account'),
        content: Text(
          user.isActive
              ? 'Are you sure you want to deactivate ${user.name}\'s account? They will lose access until restored.'
              : 'Are you sure you want to restore ${user.name}\'s account?',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(dialogCtx).pop(),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: user.isActive ? AppColors.error : AppColors.success,
            ),
            onPressed: () {
              Navigator.of(dialogCtx).pop();
              controller.toggleUserStatus(user);
            },
            child: Text(actionName, style: const TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }
}
