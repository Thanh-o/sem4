import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:your_app/register_screen.dart'; // Replace with actual path to RegisterScreen

void main() {
  group('Facebook Registration Screen Tests', () {
    // Helper function to set up the widget
    Future<void> setUpWidget(WidgetTester tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: RegisterScreen(),
        ),
      );
      await tester.pumpAndSettle();
    }

    testWidgets('TC01: Verify UI elements are displayed correctly', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Verify logo
      expect(find.byKey(Key('facebookLogo')), findsOneWidget);
      // Verify title
      expect(find.text('Create a new account'), findsOneWidget);
      // Verify input fields
      expect(find.byKey(Key('firstNameField')), findsOneWidget);
      expect(find.byKey(Key('surnameField')), findsOneWidget);
      expect(find.byKey(Key('dayDropdown')), findsOneWidget);
      expect(find.byKey(Key('monthDropdown')), findsOneWidget);
      expect(find.byKey(Key('yearDropdown')), findsOneWidget);
      expect(find.byKey(Key('genderFemaleRadio')), findsOneWidget);
      expect(find.byKey(Key('mobileEmailField')), findsOneWidget);
      expect(find.byKey(Key('passwordField')), findsOneWidget);
      // Verify Sign Up button
      expect(find.byKey(Key('signUpButton')), findsOneWidget);
    });

    testWidgets('TC02: Check mandatory First Name field validation', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Leave First Name empty, fill other fields
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify error icon/text for First Name
      expect(find.textContaining('!'), findsOneWidget);
    });

    testWidgets('TC05: Verify age under 13 is invalid', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Set Date of Birth to 2020 (age < 13 in 2025)
      await tester.tap(find.byKey(Key('dayDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('1'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('monthDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('January'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('yearDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('2020'));
      await tester.pumpAndSettle();

      // Fill other fields
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify age error
      expect(find.text('Must be at least 13 years old.'), findsOneWidget);
    });

    testWidgets('TC06: Verify age over 13 is valid', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Set Date of Birth to 2000 (age > 13 in 2025)
      await tester.tap(find.byKey(Key('dayDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('1'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('monthDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('January'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('yearDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('2000'));
      await tester.pumpAndSettle();

      // Fill other fields
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify no age error
      expect(find.text('Must be at least 13 years old.'), findsNothing);
    });

    testWidgets('TC09: Check mandatory Mobile/Email field validation', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Leave Mobile/Email empty, fill other fields
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify error icon/text for Mobile/Email
      expect(find.textContaining('!'), findsOneWidget);
    });

    testWidgets('TC11: Verify password under 8 characters is invalid', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Enter password with <8 characters
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'pass123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify error icon/text for Password
      expect(find.textContaining('!'), findsOneWidget);
    });

    testWidgets('TC12: Verify password with 8+ characters is valid', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Enter password with >=8 characters
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify no error for Password
      expect(find.textContaining('!'), findsNothing);
    });

    testWidgets('TC13: Verify successful registration with valid data', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Enter valid data
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.tap(find.byKey(Key('dayDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('1'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('monthDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('January'));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(Key('yearDropdown')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('1990'));
      await tester.pumpAndSettle();
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'john@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify success SnackBar
      expect(find.text('Account created successfully!'), findsOneWidget);
    });

    testWidgets('TC16: Verify invalid email format', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Enter invalid email
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'invalid_email');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify error icon/text for Mobile/Email
      expect(find.textContaining('!'), findsOneWidget);
    });

    testWidgets('TC26: Verify valid email format', (WidgetTester tester) async {
      await setUpWidget(tester);

      // Enter valid email
      await tester.enterText(find.byKey(Key('firstNameField')), 'John');
      await tester.enterText(find.byKey(Key('surnameField')), 'Doe');
      await tester.enterText(find.byKey(Key('mobileEmailField')), 'test.user@example.com');
      await tester.enterText(find.byKey(Key('passwordField')), 'password123');
      await tester.tap(find.byKey(Key('genderMaleRadio')));
      await tester.tap(find.byKey(Key('signUpButton')));
      await tester.pump();

      // Verify no error for Mobile/Email
      expect(find.textContaining('!'), findsNothing);
    });
  });
}