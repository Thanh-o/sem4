import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class CreatePostScreen extends StatefulWidget {
  @override
  _CreatePostScreenState createState() => _CreatePostScreenState();
}

class _CreatePostScreenState extends State<CreatePostScreen> {
  final _formKey = GlobalKey<FormState>();
  String name = '', location = '', content = '', image = '';
  final storage = FlutterSecureStorage();

  Future<String?> _fetchUserName() async {
    final userId = await storage.read(key: 'mock_token');
    if (userId == null) return null;

    final userUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/users/$userId');
    final response = await http.get(userUrl);

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['name'];
    } else {
      return null;
    }
  }

  Future<void> submitPost() async {
    final now = DateTime.now();
    final userId = await storage.read(key: 'mock_token');
    final fetchedName = await _fetchUserName();

    if (userId == null || fetchedName == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Không thể lấy thông tin người dùng')),
      );
      return;
    }

    final postUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/posts');

    final body = {
      "name": fetchedName,
      "location": location,
      "time": "${now.hour}:${now.minute} ${now.day}/${now.month}/${now.year}",
      "content": content,
      "image": image,
      "likes": 0,
      "comments": 0,
      "bookmarked": 0,
      "userId": userId  // <-- LIÊN KẾT VỚI USER
    };

    final response = await http.post(postUrl, body: json.encode(body), headers: {
      'Content-Type': 'application/json',
    });

    if (response.statusCode == 201) {
      Navigator.pop(context, true);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Lỗi khi tạo bài viết')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Tạo bài viết'), backgroundColor: Colors.pinkAccent),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(decoration: const InputDecoration(labelText: 'Địa điểm'), onChanged: (val) => location = val),
              TextFormField(decoration: const InputDecoration(labelText: 'Nội dung'), onChanged: (val) => content = val),
              TextFormField(decoration: const InputDecoration(labelText: 'Ảnh (URL)'), onChanged: (val) => image = val),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  if (_formKey.currentState!.validate()) submitPost();
                },
                child: const Text('Đăng bài'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
