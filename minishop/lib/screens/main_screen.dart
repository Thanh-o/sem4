import 'package:flutter/material.dart';
import 'package:minishop/screens/home_screen.dart';
import 'package:minishop/screens/social_feed_screen.dart';
import 'package:minishop/screens/cart_screen.dart';
import 'package:minishop/screens/history_screen.dart';
import 'package:minishop/screens/create_post_screen.dart'; // Thêm dòng này

class MainScreen extends StatefulWidget {
  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _selectedIndex = 0;

  // Tạo key để truy cập state của SocialFeedScreen
  final GlobalKey<SocialFeedScreenState> socialFeedKey = GlobalKey<SocialFeedScreenState>();

  late final List<Widget> _screens;

  @override
  void initState() {
    super.initState();
    _screens = [
      HomeScreen(),
      SocialFeedScreen(key: socialFeedKey),  // Gán key ở đây
      CartScreen(),
      HistoryScreen(),
    ];
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_selectedIndex],
      floatingActionButton: _selectedIndex == 1
          ? FloatingActionButton(
        backgroundColor: Colors.pinkAccent,
        child: Icon(Icons.add),
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => CreatePostScreen()),
          );
          if (result == true) {
            // Gọi reloadPosts() qua key
            socialFeedKey.currentState?.reloadPosts();
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
        ],
      ),
    );
  }
}

