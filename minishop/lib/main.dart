import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';
import 'package:minishop/providers/product_provider.dart';
import 'package:minishop/screens/home_screen.dart'; // <--- mới
import 'package:minishop/screens/history_screen.dart';
import 'package:minishop/screens/social_feed_screen.dart';
import 'package:minishop/screens/cart_screen.dart';
import 'package:minishop/screens/checkout_screen.dart';
import 'package:minishop/screens/main_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
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
        home: MainScreen(), // Dùng MainScreen làm home

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
