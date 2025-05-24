import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:minishop/screens/home_screen.dart';
import 'package:minishop/screens/social_feed_screen.dart';
import 'package:minishop/screens/cart_screen.dart';
import 'package:minishop/screens/history_screen.dart';
import 'package:minishop/screens/create_post_screen.dart';
import 'package:minishop/screens/login_screen.dart';
import 'package:minishop/screens/profile_screen.dart';

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
      ProfileScreen(),
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
    switch (index) {
      case 0:
      case 1:
      case 2:
        setState(() {
          _selectedIndex = index;
        });
        break;
      case 3: // Post
        final result = await Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => CreatePostScreen()),
        );
        if (result == true) {
          socialFeedKey.currentState?.fetchPosts();
        }
        break;
      case 4: // Logout
        _logout();
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _selectedIndex < 3 ? _screens[_selectedIndex] : Container(),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.description), label: 'Feed'),

          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
          BottomNavigationBarItem(icon: Icon(Icons.edit), label: 'Post'), // Biểu tượng cây bút
          BottomNavigationBarItem(icon: Icon(Icons.logout), label: 'Logout'),
        ],
      ),
    );
  }
}
