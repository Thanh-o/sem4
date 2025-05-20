import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class CreatePostScreen extends StatefulWidget {
  @override
  _CreatePostScreenState createState() => _CreatePostScreenState();
}

class _CreatePostScreenState extends State<CreatePostScreen> {
  final _formKey = GlobalKey<FormState>();
  String name = '', location = '', content = '', image = '';

  Future<void> submitPost() async {
    final url = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/posts');
    final now = DateTime.now();

    final body = {
      "name": name,
      "location": location,
      "time": "${now.hour}:${now.minute} ${now.day}/${now.month}/${now.year}",
      "content": content,
      "image": image,
      "likes": 0,
      "comments": 0,
      "bookmarked": 0
    };

    final response = await http.post(url, body: json.encode(body), headers: {
      'Content-Type': 'application/json',
    });

    if (response.statusCode == 201) {
      Navigator.pop(context, true); // Trả về để load lại bài viết
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Lỗi khi tạo bài viết')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Tạo bài viết'), backgroundColor: Colors.pinkAccent),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(decoration: InputDecoration(labelText: 'Tên'), onChanged: (val) => name = val),
              TextFormField(decoration: InputDecoration(labelText: 'Địa điểm'), onChanged: (val) => location = val),
              TextFormField(decoration: InputDecoration(labelText: 'Nội dung'), onChanged: (val) => content = val),
              TextFormField(decoration: InputDecoration(labelText: 'Ảnh (URL)'), onChanged: (val) => image = val),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  if (_formKey.currentState!.validate()) submitPost();
                },
                child: Text('Đăng bài'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
