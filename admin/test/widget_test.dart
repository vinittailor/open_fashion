import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:open_fashion_admin/main.dart';

void main() {
  testWidgets('OpenFashionAdminApp basic smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(
      const ProviderScope(
        child: OpenFashionAdminApp(),
      ),
    );

    // Verify that the title is rendered.
    expect(find.textContaining('OPEN FASHION'), findsOneWidget);
  });
}
