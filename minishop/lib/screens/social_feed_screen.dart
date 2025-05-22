import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'post_detail_screen.dart';

class SocialFeedScreen extends StatefulWidget {
  const SocialFeedScreen({Key? key}) : super(key: key);

  @override
  SocialFeedScreenState createState() => SocialFeedScreenState();
}

class SocialFeedScreenState extends State<SocialFeedScreen> {
  final storage = FlutterSecureStorage();
  String? userId;
  List<dynamic> posts = [];
  List<dynamic> users = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    initUserAndFetch();
  }

  Future<void> initUserAndFetch() async {
    userId = await storage.read(key: 'mock_token');
    await fetchPosts();
  }

  Future<void> fetchPosts() async {
    setState(() => isLoading = true);
    final postsUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/posts');
    final usersUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/users');

    try {
      final postsResponse = await http.get(postsUrl);
      final usersResponse = await http.get(usersUrl);

      if (postsResponse.statusCode == 200 && usersResponse.statusCode == 200) {
        final List<dynamic> postsData = json.decode(utf8.decode(postsResponse.bodyBytes));
        users = json.decode(utf8.decode(usersResponse.bodyBytes));

        for (var post in postsData) {
          final user = users.firstWhere((u) => u['id'] == post['userId'], orElse: () => null);
          post['name'] = user?['name'] ?? 'Ẩn danh';
          post['avatar'] = user?['avatar'];
          post['likes'] = users.where((u) => (u['likedPosts'] ?? []).contains(post['id'])).length;
          post['bookmarked'] = users.where((u) => (u['bookmarkedPosts'] ?? []).contains(post['id'])).length;
          post['likedBy'] = users.where((u) => (u['likedPosts'] ?? []).contains(post['id'])).map((u) => u['id']).toList();
          post['bookmarkedBy'] = users.where((u) => (u['bookmarkedPosts'] ?? []).contains(post['id'])).map((u) => u['id']).toList();
        }

        setState(() {
          posts = postsData;
          isLoading = false;
        });
      }
    } catch (e) {
      print('Error: $e');
      setState(() => isLoading = false);
    }
  }

  Future<void> toggleLike(String postId, bool isLiked) async {
    if (userId == null) return;
    final userUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/users/$userId');
    final response = await http.get(userUrl);

    if (response.statusCode == 200) {
      final userData = json.decode(response.body);
      List liked = List.from(userData['likedPosts'] ?? []);

      isLiked ? liked.add(postId) : liked.remove(postId);

      await http.put(userUrl,
          headers: {'Content-Type': 'application/json'},
          body: json.encode({'likedPosts': liked}));

      fetchPosts();
    }
  }

  Future<void> toggleBookmark(String postId, bool isBookmarked) async {
    if (userId == null) return;
    final userUrl = Uri.parse('https://682bf191d29df7a95be4ecee.mockapi.io/users/$userId');
    final response = await http.get(userUrl);

    if (response.statusCode == 200) {
      final userData = json.decode(response.body);
      List bookmarks = List.from(userData['bookmarkedPosts'] ?? []);

      isBookmarked ? bookmarks.add(postId) : bookmarks.remove(postId);

      await http.put(userUrl,
          headers: {'Content-Type': 'application/json'},
          body: json.encode({'bookmarkedPosts': bookmarks}));

      fetchPosts();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hàng hiệu cao cấp'),
        backgroundColor: Colors.pinkAccent,
        actions: [
          IconButton(
            icon: isLoading
                ? const SizedBox(
              width: 20,
              height: 20,
              child: CircularProgressIndicator(
                  strokeWidth: 2, color: Colors.white),
            )
                : const Icon(Icons.refresh),
            onPressed: isLoading ? null : fetchPosts,
          ),
        ],
      ),
      backgroundColor: Colors.grey[100],
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
        onRefresh: fetchPosts,
        child: ListView.builder(
          padding: const EdgeInsets.all(16),
          physics: const AlwaysScrollableScrollPhysics(),
          itemCount: posts.length,
          itemBuilder: (context, index) {
            final post = posts[index];
            final bool isLiked =
            (post['likedBy'] ?? []).contains(userId);
            final bool isBookmarked =
            (post['bookmarkedBy'] ?? []).contains(userId);

            return GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (_) => PostDetailScreen(post: post)),
                );
              },
              child: Card(
                margin: const EdgeInsets.only(bottom: 16),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20)),
                elevation: 6,
                shadowColor: Colors.black12,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          CircleAvatar(
                            radius: 24,
                            backgroundColor: Colors.grey.shade200,
                            backgroundImage: post['avatar'] != null
                                ? NetworkImage(post['avatar'])
                                : const AssetImage('assets/user.jpg')
                            as ImageProvider,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment:
                              CrossAxisAlignment.start,
                              children: [
                                Text(post['name'],
                                    style: const TextStyle(
                                        fontWeight: FontWeight.bold,
                                        fontSize: 16)),
                                Text('${post['time']} • ${post['location']}',
                                    style: TextStyle(
                                        color: Colors.grey[600],
                                        fontSize: 13)),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Text(post['content'],
                          style: const TextStyle(fontSize: 14)),
                      if (post['image'] != null &&
                          post['image'].toString().isNotEmpty)
                        Padding(
                          padding: const EdgeInsets.only(top: 12),
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(16),
                            child: Image.network(
                              post['image'],
                              height: 200,
                              width: double.infinity,
                              fit: BoxFit.cover,
                              errorBuilder: (_, __, ___) => Container(
                                height: 200,
                                color: Colors.grey.shade300,
                                alignment: Alignment.center,
                                child: const Icon(Icons.broken_image,
                                    size: 48, color: Colors.grey),
                              ),
                            ),
                          ),
                        ),
                      const SizedBox(height: 12),
                      Wrap(
                        spacing: 12,
                        children: [
                          _buildChip(
                            isLiked
                                ? Icons.favorite
                                : Icons.favorite_border,
                            '${post['likes']} Thích',
                            Colors.redAccent,
                                () =>
                                toggleLike(post['id'], !isLiked),
                          ),
                          _buildChip(
                            Icons.comment,
                            '${post['comments']} Bình luận',
                            Colors.blueAccent,
                                () {},
                          ),
                          _buildChip(
                            isBookmarked
                                ? Icons.bookmark
                                : Icons.bookmark_border,
                            '${post['bookmarked']}',
                            Colors.orangeAccent,
                                () => toggleBookmark(
                                post['id'], !isBookmarked),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget _buildChip(
      IconData icon, String label, Color color, VoidCallback onTap) {
    return InkWell(
      borderRadius: BorderRadius.circular(24),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(24),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 18, color: color),
            const SizedBox(width: 6),
            Text(label,
                style: TextStyle(
                    color: color, fontWeight: FontWeight.w500, fontSize: 13)),
          ],
        ),
      ),
    );
  }
}
