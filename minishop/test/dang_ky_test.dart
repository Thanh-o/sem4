import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:minishop/signup_screen.dart'; // Adjust based on your project structure

void main() {
  setUpAll(() {
    print('Starting tests for DangKyManHinh...');
  });

  tearDownAll(() {
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

  testWidgets('Invalid email format shows error', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(TextFormField).at(2));
    await tester.enterText(find.byType(TextFormField).at(2), 'invalid');
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

    print('thành công rồi anh ưi: Invalid email format test passed');
  });

  testWidgets('Minimum valid age (13 years) passes validation', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    final currentYear = DateTime.now().year;
    final minValidYear = (currentYear - 13).toString();

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
    await tester.ensureVisible(find.text(minValidYear));
    await tester.tap(find.text(minValidYear).last);
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Sign Up'));
    await tester.tap(find.text('Sign Up'), warnIfMissed: false);
    await tester.pumpAndSettle();

    expect(find.text('Must be at least 13 years old.'), findsNothing);

    print('thành công rồi anh ưi: Minimum valid age test passed');
  });

  testWidgets('Custom gender selection works', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Custom'));
    await tester.tap(find.text('Custom'));
    await tester.pumpAndSettle();

    expect(
      find.byWidgetPredicate(
            (widget) => widget is Radio<String> && widget.groupValue == 'Custom' && widget.value == 'Custom',
      ),
      findsOneWidget,
    );

    print('thành công rồi anh ưi: Custom gender selection test passed');
  });

  testWidgets('Focus navigation through input fields', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    // Start at First Name
    await tester.ensureVisible(find.byType(TextFormField).at(0));
    await tester.tap(find.byType(TextFormField).at(0));
    await tester.pumpAndSettle();
    await tester.enterText(find.byType(TextFormField).at(0), 'John');
    expect(tester.widget<TextFormField>(find.byType(TextFormField).at(0)).controller!.text, 'John');

    // Tab to Surname
    await tester.sendKeyEvent(LogicalKeyboardKey.tab);
    await tester.pumpAndSettle();
    await tester.enterText(find.byType(TextFormField).at(1), 'Doe');
    expect(tester.widget<TextFormField>(find.byType(TextFormField).at(1)).controller!.text, 'Doe');

    // Tab to Email
    await tester.sendKeyEvent(LogicalKeyboardKey.tab);
    await tester.pumpAndSettle();
    await tester.enterText(find.byType(TextFormField).at(2), 'john@example.com');
    expect(tester.widget<TextFormField>(find.byType(TextFormField).at(2)).controller!.text, 'john@example.com');

    // Tab to Password
    await tester.sendKeyEvent(LogicalKeyboardKey.tab);
    await tester.pumpAndSettle();
    await tester.enterText(find.byType(TextFormField).at(3), 'password123');
    expect(tester.widget<TextFormField>(find.byType(TextFormField).at(3)).controller!.text, 'password123');

    print('thành công rồi anh ưi: Focus navigation test passed');
  });

  testWidgets('Password exact length (8 characters) passes validation', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.byType(TextFormField).at(3));
    await tester.enterText(find.byType(TextFormField).at(3), '12345678');
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
      findsNothing,
    );

    print('thành công rồi anh ưi: Password exact length test passed');
  });

  testWidgets('Empty DOB with other valid fields shows error', (WidgetTester tester) async {
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

    await tester.ensureVisible(find.text('Male'));
    await tester.tap(find.text('Male'));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Sign Up'));
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
      findsAtLeastNWidgets(1),
    );

    expect(find.text('Account created successfully!'), findsNothing);

    print('thành công rồi anh ưi: Empty DOB test passed');
  });

  testWidgets('Terms and conditions link interaction', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    await tester.ensureVisible(find.text('Terms, Privacy Policy'));
    await tester.tap(find.text('Terms, Privacy Policy'), warnIfMissed: false);
    await tester.pumpAndSettle();

    expect(find.text('Terms link tapped'), findsNothing); // Update if UI adds action

    print('thành công rồi anh ưi: Terms link interaction test passed');
  });
  testWidgets('UI matches design with correct colors and layout', (WidgetTester tester) async {
    await tester.pumpWidget(MaterialApp(home: DangKyManHinh()));
    await tester.pumpAndSettle();

    // Verify the "facebook" logo text color (blue in the screenshot, assuming #1877F2)
    final facebookTextFinder = find.text('facebook');
    expect(facebookTextFinder, findsOneWidget);
    final facebookTextWidget = tester.widget<Text>(facebookTextFinder);
    expect(facebookTextWidget.style?.color, Color(0xFF1877F2)); // Assuming the color is set to Facebook's blue

    // Verify the "Sign Up" button color (green in the screenshot, assuming #42B72A)
    final signUpButtonFinder = find.widgetWithText(ElevatedButton, 'Sign Up');
    expect(signUpButtonFinder, findsOneWidget);
    final signUpButtonWidget = tester.widget<ElevatedButton>(signUpButtonFinder);
    expect(signUpButtonWidget.style?.backgroundColor?.resolve({}), Color(0xFF42B72A)); // Green color

    // Verify the error indicators (red circles with "!") after tapping Sign Up with empty fields
    await tester.tap(signUpButtonFinder, warnIfMissed: false);
    await tester.pumpAndSettle();
    final errorIndicatorFinder = find.byWidgetPredicate(
          (widget) =>
      widget is Container &&
          widget.decoration is BoxDecoration &&
          (widget.decoration as BoxDecoration).color == Colors.red &&
          widget.child is Text &&
          (widget.child as Text).data == '!',
    );
    expect(errorIndicatorFinder, findsAtLeastNWidgets(2)); // Date of Birth and Gender are required

    // Verify the layout structure: Check if "Create a new account" is above the form fields
    final createAccountTextFinder = find.text('Create a new account');
    expect(createAccountTextFinder, findsOneWidget);
    final firstNameFieldFinder = find.byType(TextFormField).at(0);
    expect(firstNameFieldFinder, findsOneWidget);
    final createAccountPosition = tester.getCenter(createAccountTextFinder);
    final firstNamePosition = tester.getCenter(firstNameFieldFinder);
    expect(createAccountPosition.dy < firstNamePosition.dy, isTrue); // "Create a new account" should be above the first name field

    // Verify gender radio buttons are in a horizontal row
    final genderRowFinder = find.byWidgetPredicate(
          (widget) =>
      widget is Row &&
          widget.children.any((child) => child is Radio<String> && child.value == 'Female') &&
          widget.children.any((child) => child is Radio<String> && child.value == 'Male') &&
          widget.children.any((child) => child is Radio<String> && child.value == 'Custom'),
    );
    expect(genderRowFinder, findsOneWidget);

    print('thành công rồi anh ưi: UI design test passed');
  });
}