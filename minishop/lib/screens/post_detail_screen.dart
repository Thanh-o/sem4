import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class PostDetailScreen extends StatefulWidget {
  final Map<String, dynamic> post;

  const PostDetailScreen({Key? key, required this.post}) : super(key: key);

  @override
  State<PostDetailScreen> createState() => _PostDetailScreenState();
}

class _PostDetailScreenState extends State<PostDetailScreen> {
  final TextEditingController _commentController = TextEditingController();
  final FlutterSecureStorage secureStorage = const FlutterSecureStorage();
  List<Map<String, String>> comments = [];
  Map<String, dynamic> userMap = {}; // userId => userName

  @override
  void initState() {
    super.initState();
    fetchAllCommentsAndUsers();
  }

  Future<void> fetchAllCommentsAndUsers() async {
    await fetchUsers();
    loadCommentsFromPost();
  }

  Future<void> fetchUsers() async {
    final url = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/users');

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final List<dynamic> usersData = json.decode(response.body);
        userMap = {
          for (var user in usersData) user['id'].toString(): user['name'].toString()
        };
      }
    } catch (e) {
      print('Lỗi khi lấy user: $e');
    }
  }

  void loadCommentsFromPost() {
    final List<dynamic> commentData = widget.post['commentList'] ?? [];

    setState(() {
      comments = commentData.map<Map<String, String>>((comment) {
        final userId = comment['userId'];
        return {
          'user': userMap[userId] ?? 'Ẩn danh',
          'text': comment['text'],
        };
      }).toList();
    });
  }

  Future<void> addCommentToPost(String text) async {
    if (text.trim().isEmpty) return;

    final String? userId = await secureStorage.read(key: 'mock_token');

    if (userId == null) {
      print('Chưa đăng nhập');
      return;
    }

    final postId = widget.post['id'];
    final url = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/posts/$postId');

    try {
      final newComment = {
        'userId': userId,
        'text': text,
      };

      final updatedCommentList = List.from(widget.post['commentList'] ?? [])..add(newComment);

      final updatedPost = {
        'commentList': updatedCommentList,
        'comments': updatedCommentList.length,
      };

      final response = await http.put(
        url,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(updatedPost),
      );

      if (response.statusCode == 200) {
        print('Thêm bình luận thành công');
        setState(() {
          comments.add({'user': userMap[userId] ?? 'Bạn', 'text': text});
          _commentController.clear();
        });
        widget.post['commentList'] = updatedCommentList;
        widget.post['comments'] = updatedCommentList.length;
      } else {
        print('Lỗi khi cập nhật post: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi gửi comment lên server: $e');
    }
  }

  void addComment(String text) {
    addCommentToPost(text);
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
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [

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
                        child: const Center(child: Icon(Icons.broken_image, size: 60)),
                      ),
                    ),
                  ),


                  const SizedBox(height: 16),
                  Row(children: [const Icon(Icons.favorite_border), const SizedBox(width: 4), Text('${post['likes']} lượt thích')]),
                  const SizedBox(height: 8),
                  Row(children: [const Icon(Icons.comment_outlined), const SizedBox(width: 4), Text('${post['comments'] ?? 0} bình luận')]),
                  const SizedBox(height: 8),
                  Row(children: [const Icon(Icons.bookmark_border), const SizedBox(width: 4), Text('${post['bookmarked']} đã lưu')]),
                  const SizedBox(height: 20),
                  Text('Đăng bởi: ${post['name']}', style: const TextStyle(fontStyle: FontStyle.italic)),
                  Text('Địa điểm: ${post['location']}'),
                  Text('Thời gian: ${post['time']}'),
                  const SizedBox(height: 12),
                  Text('Nội dung : ${post['content']}', style: const TextStyle(fontSize: 18)),
                  const SizedBox(height: 20),
                  const Text('Bình luận', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const Divider(),
                  ...comments.map((comment) => ListTile(
                    leading: const CircleAvatar(child: Icon(Icons.person)),
                    title: Text(comment['user']!),
                    subtitle: Text(comment['text']!),
                  )),
                ],
              ),
            ),
          ),
          const Divider(height: 1),
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
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.send, color: Colors.pinkAccent),
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
