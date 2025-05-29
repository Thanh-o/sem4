import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:minishop/signup_screen.dart';
 // Adjust based on your project structure

void main() {
  setUpAll(() {
    print('Starting tests for DangKyManHinh...');
  });

  tearDownAll(() {
    // Note: We can't directly check if all tests passed here, but this runs after all tests
    print('All tests completed.');
  });

  testWidgets('DangKyManHinh displays all required UI elements', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    expect(find.text('facebook'), findsOneWidget);
    expect(find.text('Create a new account'), findsOneWidget);
    expect(find.text("It's quick and easy."), findsOneWidget);
    expect(find.text('First name'), findsOneWidget);
    expect(find.text('Surname'), findsOneWidget);
    expect(find.text('Mobile number or email'), findsOneWidget);
    expect(find.text('New password'), findsOneWidget);
    expect(find.text('Day'), findsOneWidget);
    expect(find.text('Month'), findsOneWidget);
    expect(find.text('Year'), findsOneWidget);
    expect(find.text('Female'), findsOneWidget);
    expect(find.text('Male'), findsOneWidget);
    expect(find.text('Custom'), findsOneWidget);
    expect(find.text('Sign Up'), findsOneWidget);
    expect(find.textContaining('By clicking Sign Up'), findsOneWidget);
    expect(find.text('Already have an account?'), findsOneWidget);

    print('thành công rồi anh ưi: UI elements test passed');
  });

  testWidgets('Form validation shows errors for empty required fields', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Sign Up'));
    await tester.pumpAndSettle();

    await tester.tap(find.text('Sign Up'), warnIfMissed: false);
    await tester.pumpAndSettle();

    expect(
      find.byWidgetPredicate(
            (widget) =>
        widget is Container &&
            widget.decoration is BoxDecoration &&
            (widget.decoration as BoxDecoration).color == Colors.red &&
            widget.child is Text &&
            (widget.child as Text).data == '!',
      ),
      findsAtLeastNWidgets(2),
    );

    expect(find.text('Account created successfully!'), findsNothing);

    print('thành công rồi anh ưi: Empty fields validation test passed');
  });

  testWidgets('Form validation for Date of Birth (age < 13)', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(0));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(0));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('1'));
    await tester.tap(find.text('1').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(1));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(1));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('Jan'));
    await tester.tap(find.text('Jan').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(2));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(2));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('2024'));
    await tester.tap(find.text('2024').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Sign Up'));
    await tester.tap(find.text('Sign Up'), warnIfMissed: false);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Must be at least 13 years old.'));
    expect(find.text('Must be at least 13 years old.'), findsOneWidget);

    print('thành công rồi anh ưi: DOB validation test passed');
  });

  testWidgets('Password validation for length < 8', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(TextFormField).at(3));
    await tester.enterText(find.byType(TextFormField).at(3), 'short');
    await tester.pumpAndSettle();

    expect(
      find.byWidgetPredicate(
            (widget) =>
        widget is Container &&
            widget.decoration is BoxDecoration &&
            (widget.decoration as BoxDecoration).color == Colors.red &&
            widget.child is Text &&
            (widget.child as Text).data == '!',
      ),
      findsAtLeastNWidgets(1),
    );

    print('thành công rồi anh ưi: Password validation test passed');
  });

  testWidgets('Successful form submission with valid data', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(TextFormField).at(0));
    await tester.enterText(find.byType(TextFormField).at(0), 'John');
    await tester.ensureVisible(find.byType(TextFormField).at(1));
    await tester.enterText(find.byType(TextFormField).at(1), 'Doe');
    await tester.ensureVisible(find.byType(TextFormField).at(2));
    await tester.enterText(find.byType(TextFormField).at(2), 'john.doe@example.com');
    await tester.ensureVisible(find.byType(TextFormField).at(3));
    await tester.enterText(find.byType(TextFormField).at(3), 'password123');

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(0));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(0));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('1'));
    await tester.tap(find.text('1').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(1));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(1));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('Jan'));
    await tester.tap(find.text('Jan').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(DropdownButtonFormField<String>).at(2));
    await tester.tap(find.byType(DropdownButtonFormField<String>).at(2));
    await tester.pumpAndSettle();
    await tester.ensureVisible(find.text('2000'));
    await tester.tap(find.text('2000').last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Male'));
    await tester.tap(find.text('Male'));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Sign Up'));
    await tester.tap(find.text('Sign Up'), warnIfMissed: false);
    await tester.pumpAndSettle();

    expect(find.text('Account created successfully!'), findsOneWidget);

    print('thành công rồi anh ưi: Successful submission test passed');
  });

  testWidgets('Already have an account link triggers redirect', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Already have an account?'));
    await tester.tap(find.text('Already have an account?'), warnIfMissed: false);
    await tester.pumpAndSettle();

    expect(find.text('Redirecting to login...'), findsOneWidget);

    print('thành công rồi anh ưi: Login link test passed');
  });
}