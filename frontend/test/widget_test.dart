import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'package:florent/main.dart';

void main() {
  testWidgets('App renders without error', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: FlorentApp()));
    await tester.pumpAndSettle();

    expect(find.text('Florent'), findsOneWidget);
  });
}
