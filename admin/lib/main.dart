import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/app_theme.dart';
import 'core/theme/app_colors.dart';
import 'core/theme/app_typography.dart';
import 'core/constants/breakpoints.dart';
import 'features/auth/presentation/controllers/auth_controller.dart';
import 'features/auth/presentation/screens/login_screen.dart';
import 'features/auth/presentation/screens/register_screen.dart';
import 'features/profile/presentation/widgets/profile_modal.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    // ProviderScope stores the state of all Riverpod providers
    const ProviderScope(
      child: OpenFashionAdminApp(),
    ),
  );
}

class OpenFashionAdminApp extends ConsumerWidget {
  const OpenFashionAdminApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeMode = ref.watch(themeModeProvider);
    final authState = ref.watch(authControllerProvider);

    return MaterialApp(
      title: 'Open Fashion Admin',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: themeMode,
      home: _buildHome(authState),
    );
  }

  Widget _buildHome(AuthState authState) {
    if (authState.status == AuthStatus.initial) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(color: AppColors.accent),
        ),
      );
    }

    if (authState.isAuthenticated) {
      return const AdminShellScreen();
    }

    return const LoginScreen();
  }
}

class AdminShellScreen extends ConsumerStatefulWidget {
  const AdminShellScreen({super.key});

  @override
  ConsumerState<AdminShellScreen> createState() => _AdminShellScreenState();
}

class _AdminShellScreenState extends ConsumerState<AdminShellScreen> {
  int _selectedIndex = 0;

  final List<String> _titles = const ['Dashboard', 'Products', 'Orders', 'Customers', 'Settings'];

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final authState = ref.watch(authControllerProvider);
    final user = authState.user;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'OPEN FASHION · ${_titles[_selectedIndex].toUpperCase()}',
          style: AppTypography.titleLarge(isDark),
        ),
        actions: [
          // Authenticated User Profile & Role Chip
          if (user != null) ...[
            InkWell(
              borderRadius: BorderRadius.circular(16),
              onTap: () => showAdminProfileModal(context, user),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.accent.withValues(alpha: 0.12),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: AppColors.accent.withValues(alpha: 0.3)),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    CircleAvatar(
                      radius: 12,
                      backgroundColor: AppColors.accent,
                      child: Text(
                        user.name.isNotEmpty ? user.name[0].toUpperCase() : 'A',
                        style: const TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text(user.name, style: AppTypography.labelMedium(isDark)),
                    const SizedBox(width: 6),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: AppColors.accent,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        user.role,
                        style: const TextStyle(
                          fontSize: 9,
                          fontWeight: FontWeight.bold,
                          color: Colors.black,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(width: 8),
          ],

          // Quick Action to open Register / Invite User
          TextButton.icon(
            style: TextButton.styleFrom(foregroundColor: AppColors.accent),
            icon: const Icon(Icons.person_add_outlined, size: 18),
            label: const Text('INVITE USER', style: TextStyle(fontWeight: FontWeight.bold)),
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const RegisterScreen()),
              );
            },
          ),
          const SizedBox(width: 8),

          // Theme Mode Toggle Button
          IconButton(
            icon: Icon(isDark ? Icons.light_mode : Icons.dark_mode),
            tooltip: 'Toggle Theme Mode',
            onPressed: () => ref.read(themeModeProvider.notifier).toggleTheme(),
          ),
          const SizedBox(width: 4),

          // Logout Action Button
          IconButton(
            icon: const Icon(Icons.logout_outlined, size: 20),
            tooltip: 'Sign Out',
            onPressed: () => ref.read(authControllerProvider.notifier).logout(),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: ResponsiveLayout(
        // Desktop Layout (NavigationRail + Content Pane)
        desktop: Row(
          children: [
            NavigationRail(
              selectedIndex: _selectedIndex,
              onDestinationSelected: (index) => setState(() => _selectedIndex = index),
              labelType: NavigationRailLabelType.all,
              destinations: const [
                NavigationRailDestination(icon: Icon(Icons.dashboard_outlined), selectedIcon: Icon(Icons.dashboard), label: Text('Dashboard')),
                NavigationRailDestination(icon: Icon(Icons.inventory_2_outlined), selectedIcon: Icon(Icons.inventory_2), label: Text('Products')),
                NavigationRailDestination(icon: Icon(Icons.shopping_bag_outlined), selectedIcon: Icon(Icons.shopping_bag), label: Text('Orders')),
                NavigationRailDestination(icon: Icon(Icons.people_outline), selectedIcon: Icon(Icons.people), label: Text('Customers')),
                NavigationRailDestination(icon: Icon(Icons.settings_outlined), selectedIcon: Icon(Icons.settings), label: Text('Settings')),
              ],
            ),
            const VerticalDivider(thickness: 1, width: 1),
            Expanded(
              child: _buildBodyContent(isDark),
            ),
          ],
        ),
        // Mobile Layout (Single Column)
        mobile: _buildBodyContent(isDark),
      ),
      // Mobile Bottom Navigation Bar
      bottomNavigationBar: Breakpoints.isDesktop(context)
          ? null
          : NavigationBar(
              selectedIndex: _selectedIndex,
              onDestinationSelected: (index) => setState(() => _selectedIndex = index),
              destinations: const [
                NavigationDestination(icon: Icon(Icons.dashboard_outlined), label: 'Dashboard'),
                NavigationDestination(icon: Icon(Icons.inventory_2_outlined), label: 'Products'),
                NavigationDestination(icon: Icon(Icons.shopping_bag_outlined), label: 'Orders'),
                NavigationDestination(icon: Icon(Icons.people_outline), label: 'Customers'),
              ],
            ),
    );
  }

  Widget _buildBodyContent(bool isDark) {
    if (_selectedIndex == 3) {
      // Customers module hosts the User Registration / Invitation Form directly
      return const RegisterScreen();
    }

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.check_circle_outline, size: 64, color: AppColors.accent),
          const SizedBox(height: 16),
          Text(
            '${_titles[_selectedIndex]} Module Initialized',
            style: AppTypography.displayMedium(isDark),
          ),
          const SizedBox(height: 8),
          Text(
            'Responsive shell running with Riverpod 2.0 & Material 3',
            style: AppTypography.bodyMedium(isDark),
          ),
        ],
      ),
    );
  }
}
