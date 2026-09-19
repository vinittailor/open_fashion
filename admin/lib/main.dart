import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/app_theme.dart';
import 'core/theme/app_colors.dart';
import 'core/theme/app_typography.dart';
import 'core/constants/breakpoints.dart';

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

    return MaterialApp(
      title: 'Open Fashion Admin',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: themeMode,
      home: const AdminShellScreen(),
    );
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

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'OPEN FASHION · ${_titles[_selectedIndex].toUpperCase()}',
          style: AppTypography.titleLarge(isDark),
        ),
        actions: [
          // Theme Mode Toggle Button
          IconButton(
            icon: Icon(isDark ? Icons.light_mode : Icons.dark_mode),
            tooltip: 'Toggle Theme Mode',
            onPressed: () => ref.read(themeModeProvider.notifier).toggleTheme(),
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
              child: _buildPlaceholderContent(isDark),
            ),
          ],
        ),
        // Mobile Layout (Single Column)
        mobile: _buildPlaceholderContent(isDark),
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

  Widget _buildPlaceholderContent(bool isDark) {
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
