import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({Key? key}) : super(key: key);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final storage = const FlutterSecureStorage();
  Map<String, dynamic>? userData;
  bool isLoading = true;
  String errorMessage = '';
  bool isEditing = false;

  // Controller để sửa
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();

  Future<void> fetchUserProfile() async {
    setState(() {
      isLoading = true;
      errorMessage = '';
    });
    try {
      final userId = await storage.read(key: 'mock_token');
      if (userId == null) {
        setState(() {
          errorMessage = 'User not logged in.';
          isLoading = false;
        });
        return;
      }

      final url = 'https://682bf191d29df7a95be4ecee.mockapi.io/users/$userId';
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        final Map<String, dynamic> user = json.decode(response.body);
        setState(() {
          userData = user;
          _nameController.text = user['name'] ?? '';
          _emailController.text = user['email'] ?? '';
          isLoading = false;
          isEditing = false;
        });
      } else {
        setState(() {
          errorMessage = 'Failed to load user data (status code ${response.statusCode})';
          isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        errorMessage = 'Error: $e';
        isLoading = false;
      });
    }
  }

  Future<void> updateUserProfile() async {
    setState(() {
      isLoading = true;
      errorMessage = '';
    });

    try {
      final userId = await storage.read(key: 'mock_token');
      if (userId == null) {
        setState(() {
          errorMessage = 'User not logged in.';
          isLoading = false;
        });
        return;
      }

      final url = 'https://682bf191d29df7a95be4ecee.mockapi.io/users/$userId';
      final response = await http.put(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name': _nameController.text,
          'email': _emailController.text,
          // API có thể có các trường khác, thêm nếu cần
        }),
      );

      if (response.statusCode == 200) {
        // Cập nhật thành công, reload profile
        await fetchUserProfile();
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Profile updated successfully')));
      } else {
        setState(() {
          errorMessage = 'Failed to update user data (status code ${response.statusCode})';
          isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        errorMessage = 'Error: $e';
        isLoading = false;
      });
    }
  }

  @override
  void initState() {
    super.initState();
    fetchUserProfile();
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (errorMessage.isNotEmpty) {
      return Scaffold(
        appBar: AppBar(title: const Text('Profile')),
        body: Center(child: Text(errorMessage)),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        actions: [
          if (!isEditing)
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: () {
                setState(() {
                  isEditing = true;
                });
              },
            ),
          // Không còn nút save/cancel trên AppBar nữa
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: isEditing
            ? Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextFormField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Name'),
            ),
            const SizedBox(height: 10),
            TextFormField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
              keyboardType: TextInputType.emailAddress,
            ),
            const SizedBox(height: 20),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () async {
                      await updateUserProfile();
                    },
                    child: const Text('Save'),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: OutlinedButton(
                    onPressed: () {
                      setState(() {
                        isEditing = false;
                        _nameController.text = userData?['name'] ?? '';
                        _emailController.text = userData?['email'] ?? '';
                      });
                    },
                    child: const Text('Cancel'),
                  ),
                ),
              ],
            )
          ],
        )
            : Column(
          children: [
            if (userData?['avatar'] != null)
              CircleAvatar(
                radius: 50,
                backgroundImage: NetworkImage(userData!['avatar']),
              ),
            const SizedBox(height: 20),
            Text(
              userData?['name'] ?? 'No name',
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Text(
              userData?['email'] ?? 'No email',
              style: const TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 10),
            Text(
              userData?['createdAt'] != null
                  ? 'Joined: ${DateTime.parse(userData!['createdAt']).toLocal().toString().split(' ')[0]}'
                  : '',
              style: const TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }

}
