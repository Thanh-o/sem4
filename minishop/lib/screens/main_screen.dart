import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:minishop/screens/home_screen.dart';
import 'package:minishop/screens/social_feed_screen.dart';
import 'package:minishop/screens/cart_screen.dart';
import 'package:minishop/screens/history_screen.dart';
import 'package:minishop/screens/create_post_screen.dart';
import 'package:minishop/screens/login_screen.dart';
import 'package:minishop/screens/profile_screen.dart';  // import mới

class MainScreen extends StatefulWidget {
  @override
  _MainScreenState createState() => _MainScreenState();
}


class _MainScreenState extends State<MainScreen> {
  int _selectedIndex = 0;
  final storage = FlutterSecureStorage();
  final GlobalKey<SocialFeedScreenState> socialFeedKey = GlobalKey<SocialFeedScreenState>();

  late final List<Widget> _screens;

  @override
  void initState() {
    super.initState();
    _checkLoginStatus();
    _screens = [
      HomeScreen(),
      SocialFeedScreen(key: socialFeedKey),
      CartScreen(),
      HistoryScreen(),
      ProfileScreen(),  // Thêm màn hình Profile vào danh sách
    ];
  }

  Future<void> _checkLoginStatus() async {
    final token = await storage.read(key: 'mock_token');
    if (token == null && mounted) {
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    }
  }

  void _logout() async {
    await storage.delete(key: 'mock_token');
    if (mounted) {
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginScreen()));
    }
  }

  void _onItemTapped(int index) async {
    if (index == 5) {  // Vì bây giờ có 6 mục: 0..5, Logout sẽ là mục cuối
      _logout();
    } else {
      setState(() {
        _selectedIndex = index;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _selectedIndex < 5 ? _screens[_selectedIndex] : Container(),
      floatingActionButton: _selectedIndex == 1
          ? FloatingActionButton(
        backgroundColor: Colors.pinkAccent,
        child: const Icon(Icons.add),
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => CreatePostScreen()),
          );
          if (result == true) {
            socialFeedKey.currentState?.fetchPosts();
          }
        },
      )
          : null,
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.people), label: 'Social'),
          BottomNavigationBarItem(icon: Icon(Icons.shopping_cart), label: 'Cart'),
          BottomNavigationBarItem(icon: Icon(Icons.history), label: 'History'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),  // Thêm Profile
          BottomNavigationBarItem(icon: Icon(Icons.logout), label: 'Logout'),
        ],
      ),
    );
  }
}


