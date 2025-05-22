import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';
import 'package:minishop/providers/product_provider.dart';
import 'package:minishop/screens/home_screen.dart';
import 'package:minishop/screens/history_screen.dart';
import 'package:minishop/screens/social_feed_screen.dart';
import 'package:minishop/screens/cart_screen.dart';
import 'package:minishop/screens/checkout_screen.dart';
import 'package:minishop/screens/main_screen.dart';
import 'package:minishop/screens/login_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  MyApp({super.key});

  Future<Widget> _getInitialScreen() async {
    final token = await _storage.read(key: 'mock_token'); // hoặc 'jwt'
    return token != null ? MainScreen() : const LoginScreen();
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ProductProvider()),
        ChangeNotifierProvider(create: (_) => CartProvider()),
      ],
      child: MaterialApp(
        title: 'Shopping App',
        theme: ThemeData(
          primarySwatch: Colors.blue,
          useMaterial3: true,
        ),
        home: FutureBuilder<Widget>(
          future: _getInitialScreen(),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Scaffold(
                body: Center(child: CircularProgressIndicator()),
              );
            } else if (snapshot.hasData) {
              return snapshot.data!;
            } else {
              return const Scaffold(
                body: Center(child: Text('Unexpected error loading app')),
              );
            }
          },
        ),

        routes: {
          '/home': (context) => HomeScreen(),
          '/history': (context) => HistoryScreen(),
          '/cart': (context) => CartScreen(),
          '/checkout': (context) => CheckoutScreen(),
          '/social': (context) => SocialFeedScreen(),
        },
      ),
    );
  }
}
