import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class PostDetailScreen extends StatefulWidget {
  final Map<String, dynamic> post;

  const PostDetailScreen({Key? key, required this.post}) : super(key: key);

  @override
  State<PostDetailScreen> createState() => _PostDetailScreenState();
}

class _PostDetailScreenState extends State<PostDetailScreen> {
  List<Map<String, String>> comments = [];
  final TextEditingController _commentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    fetchComments();
  }

  Future<void> fetchComments() async {
    final postId = widget.post['id'];
    final url = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/comments?postId=$postId');

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final List<dynamic> commentData = json.decode(utf8.decode(response.bodyBytes));
        setState(() {
          comments = commentData.map<Map<String, String>>((comment) => {
            'user': comment['user'].toString(),
            'text': comment['text'].toString(),
          }).toList();
        });
      } else {
        print('Lỗi lấy bình luận: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi lấy comment từ server: $e');
    }
  }

  Future<void> addCommentToServer(String text) async {
    if (text.trim().isEmpty) return;

    final postId = widget.post['id'];
    final url = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/comments');

    try {
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'postId': postId,
          'user': 'Bạn',
          'text': text,
          'time': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 201) {
        print('Thêm bình luận thành công');
        // Cập nhật UI ngay lập tức bằng cách thêm comment mới vào list
        setState(() {
          comments.add({'user': 'Bạn', 'text': text});
          _commentController.clear();
        });
      } else {
        print('Lỗi khi thêm bình luận: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi gửi comment lên server: $e');
    }
  }

  void addComment(String text) {
    addCommentToServer(text);
  }

  @override
  Widget build(BuildContext context) {
    final post = widget.post;

    return Scaffold(
      appBar: AppBar(
        title: Text(post['name']),
        backgroundColor: Colors.pinkAccent,
      ),
      body: Column(
        children: [
          Expanded(
            child: SingleChildScrollView(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(post['content'], style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500)),
                  SizedBox(height: 12),
                  ClipRRect(
                    borderRadius: BorderRadius.circular(12),
                    child: Image.network(
                      post['image'],
                      fit: BoxFit.cover,
                      width: double.infinity,
                      height: 240,
                      errorBuilder: (context, error, stackTrace) => Container(
                        height: 240,
                        color: Colors.grey[300],
                        child: Center(child: Icon(Icons.broken_image, size: 60)),
                      ),
                    ),
                  ),
                  SizedBox(height: 16),
                  Row(children: [Icon(Icons.favorite_border), SizedBox(width: 4), Text('${post['likes']} lượt thích')]),
                  SizedBox(height: 8),
                  Row(children: [Icon(Icons.comment_outlined), SizedBox(width: 4), Text('${post['comments']} bình luận')]),
                  SizedBox(height: 8),
                  Row(children: [Icon(Icons.bookmark_border), SizedBox(width: 4), Text('${post['bookmarked']} đã lưu')]),
                  SizedBox(height: 20),
                  Text('Đăng bởi: ${post['name']}', style: TextStyle(fontStyle: FontStyle.italic)),
                  Text('Địa điểm: ${post['location']}'),
                  Text('Thời gian: ${post['time']}'),
                  SizedBox(height: 20),
                  Text('Bình luận', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  Divider(),
                  ...comments.map((comment) => ListTile(
                    leading: CircleAvatar(child: Icon(Icons.person)),
                    title: Text(comment['user']!),
                    subtitle: Text(comment['text']!),
                  )),
                ],
              ),
            ),
          ),
          Divider(height: 1),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _commentController,
                    decoration: InputDecoration(
                      hintText: 'Viết bình luận...',
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(30)),
                      contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    ),
                  ),
                ),
                SizedBox(width: 8),
                IconButton(
                  icon: Icon(Icons.send, color: Colors.pinkAccent),
                  onPressed: () => addComment(_commentController.text),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

