import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/models/product.dart';
import 'package:minishop/providers/cart_provider.dart';

class ProductDetailScreen extends StatefulWidget {
  final Product product;

  const ProductDetailScreen({required this.product, Key? key}) : super(key: key);

  @override
  _ProductDetailScreenState createState() => _ProductDetailScreenState();
}

class _ProductDetailScreenState extends State<ProductDetailScreen> {
  int _quantity = 1;
  final TextEditingController _commentController = TextEditingController();
  List<Map<String, dynamic>> _comments = [];
  List<Map<String, dynamic>> _newComments = [];

  @override
  void initState() {
    super.initState();
    _loadComments();
  }

  Future<void> _loadComments() async {
    try {
      final String response = await DefaultAssetBundle.of(context).loadString('assets/comments.json');
      final data = json.decode(response);
      setState(() {
        _comments = List<Map<String, dynamic>>.from(data['comments'])
            .where((comment) => comment['productId'] == widget.product.id)
            .toList();
      });
    } catch (e) {
      print('Error loading comments: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Không thể tải bình luận'),
          duration: Duration(seconds: 2),
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        ),
      );
    }
  }

  void _addComment() {
    if (_commentController.text.trim().isNotEmpty) {
      setState(() {
        _newComments.add({
          'productId': widget.product.id,
          'user': 'Current User',
          'comment': _commentController.text.trim(),
          'timestamp': DateTime.now().toIso8601String(),
        });
        _commentController.clear();
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Bình luận đã được thêm'),
          duration: Duration(seconds: 2),
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          backgroundColor: Colors.green[600],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final allComments = [..._comments, ..._newComments]
        .where((comment) => comment['productId'] == widget.product.id)
        .toList();

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          // Enhanced SliverAppBar with gradient
          SliverAppBar(
            expandedHeight: 350,
            floating: false,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: Text(
                widget.product.name,
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                  shadows: [
                    Shadow(blurRadius: 4, color: Colors.black87, offset: Offset(2, 2)),
                  ],
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              background: Stack(
                fit: StackFit.expand,
                children: [
                  Hero(
                    tag: 'product-image-${widget.product.id}',
                    child: Image.network(
                      widget.product.image,
                      fit: BoxFit.cover,
                      errorBuilder: (context, error, stackTrace) => Container(
                        color: Colors.grey[300],
                        child: Center(child: Icon(Icons.broken_image, size: 80, color: Colors.grey[600])),
                      ),
                    ),
                  ),
                  Container(
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [Colors.transparent, Colors.black.withOpacity(0.6)],
                      ),
                    ),
                  ),
                ],
              ),
            ),
            leading: IconButton(
              icon: Icon(Icons.arrow_back_ios, color: Colors.white),
              onPressed: () => Navigator.pop(context),
            ),
            actions: [
              // Thêm nút giỏ hàng ở góc trên bên phải
              IconButton(
                icon: Stack(
                  children: [
                    Icon(Icons.shopping_cart, color: Colors.white),
                    // Hiển thị số lượng sản phẩm trong giỏ (nếu có)
                    Positioned(
                      right: 0,
                      child: Consumer<CartProvider>(
                        builder: (context, cart, child) => cart.itemCount > 0
                            ? Container(
                          padding: EdgeInsets.all(2),
                          decoration: BoxDecoration(
                            color: Colors.red,
                            shape: BoxShape.circle,
                          ),
                          constraints: BoxConstraints(
                            minWidth: 16,
                            minHeight: 16,
                          ),
                          child: Text(
                            '${cart.itemCount}',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 10,
                              fontWeight: FontWeight.bold,
                            ),
                            textAlign: TextAlign.center,
                          ),
                        )
                            : SizedBox.shrink(),
                      ),
                    ),
                  ],
                ),
                onPressed: () {
                  // Chuyển đến CartScreen
                  Navigator.pushNamed(context, '/cart');
                },
                tooltip: 'Giỏ hàng',
              ),
            ],
          ),
          // Nội dung chi tiết
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Card(
                elevation: 4,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Tên sản phẩm
                      Text(
                        widget.product.name,
                        style: theme.textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Colors.black87,
                          letterSpacing: 0.5,
                        ),
                      ),
                      const SizedBox(height: 12),

                      // Giá và đánh giá
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            '\$${widget.product.price.toStringAsFixed(2)}',
                            style: TextStyle(
                              fontSize: 26,
                              color: Colors.green[700],
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                          Row(
                            children: [
                              Icon(Icons.star, color: Colors.amber, size: 22),
                              const SizedBox(width: 4),
                              Text(
                                '4.5',
                                style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600, color: Colors.black87),
                              ),
                            ],
                          ),
                        ],
                      ),
                      const SizedBox(height: 24),

                      // Quantity Selector
                      Row(
                        children: [
                          Text(
                            'Số lượng:',
                            style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                          ),
                          const SizedBox(width: 16),
                          IconButton(
                            onPressed: () {
                              if (_quantity > 1) setState(() => _quantity--);
                            },
                            icon: Icon(Icons.remove_circle_outline, color: Colors.grey[600]),
                          ),
                          Text(
                            '$_quantity',
                            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
                          ),
                          IconButton(
                            onPressed: () => setState(() => _quantity++),
                            icon: Icon(Icons.add_circle_outline, color: Colors.grey[600]),
                          ),
                        ],
                      ),
                      const SizedBox(height: 24),

                      // Mô tả
                      Text(
                        'Mô tả sản phẩm:',
                        style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        widget.product.description.isNotEmpty
                            ? widget.product.description
                            : 'Sản phẩm chất lượng cao, phù hợp với nhu cầu hàng ngày.',
                        style: theme.textTheme.bodyLarge?.copyWith(height: 1.5, color: Colors.black87),
                      ),
                      const SizedBox(height: 16),
                      Text(
                        'Giao hàng nhanh chóng và hỗ trợ đổi trả trong vòng 7 ngày.',
                        style: theme.textTheme.bodyMedium?.copyWith(height: 1.5, color: Colors.grey[700]),
                      ),
                      const SizedBox(height: 24),

                      // Phần bình luận
                      Text(
                        'Bình luận:',
                        style: theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 12),
                      // TextField để nhập bình luận
                      Row(
                        children: [
                          CircleAvatar(
                            radius: 20,
                            backgroundColor: Colors.grey[300],
                            child: Icon(Icons.person, color: Colors.grey[600]),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: TextField(
                              controller: _commentController,
                              decoration: InputDecoration(
                                hintText: 'Viết bình luận của bạn...',
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: BorderSide(color: Colors.grey[400]!),
                                ),
                                filled: true,
                                fillColor: Colors.grey[100],
                                contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                              ),
                            ),
                          ),
                          const SizedBox(width: 12),
                          ElevatedButton(
                            onPressed: _addComment,
                            child: Text('Gửi', style: TextStyle(fontSize: 16)),
                            style: ElevatedButton.styleFrom(

                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                              padding: EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                              elevation: 2,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      // Danh sách bình luận
                      ListView.separated(
                        shrinkWrap: true,
                        physics: NeverScrollableScrollPhysics(),
                        itemCount: allComments.length,
                        separatorBuilder: (context, index) => Divider(height: 16, color: Colors.grey[300]),
                        itemBuilder: (context, index) {
                          final comment = allComments[index];
                          return AnimatedOpacity(
                            opacity: 1.0,
                            duration: Duration(milliseconds: 300),
                            child: ListTile(
                              contentPadding: EdgeInsets.zero,
                              leading: CircleAvatar(
                                radius: 20,
                                backgroundColor: Colors.blue[100],
                                child: Text(
                                  comment['user'][0].toUpperCase(),
                                  style: TextStyle(color: Colors.blue[800], fontWeight: FontWeight.bold),
                                ),
                              ),
                              title: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text(
                                    comment['user'],
                                    style: theme.textTheme.titleSmall?.copyWith(
                                      fontWeight: FontWeight.bold,
                                      color: Colors.black87,
                                    ),
                                  ),
                                  Text(
                                    comment['timestamp'] != null
                                        ? _formatTimestamp(comment['timestamp'])
                                        : 'Vừa xong',
                                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                                  ),
                                ],
                              ),
                              subtitle: Padding(
                                padding: const EdgeInsets.only(top: 4),
                                child: Text(
                                  comment['comment'],
                                  style: theme.textTheme.bodyMedium?.copyWith(
                                    height: 1.5,
                                    color: Colors.black87,
                                  ),
                                ),
                              ),
                            ),
                          );
                        },
                      ),
                      const SizedBox(height: 80),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
      floatingActionButton: AnimatedScale(
        scale: 1.0,
        duration: Duration(milliseconds: 100),
        child: FloatingActionButton.extended(
          onPressed: () {
            Provider.of<CartProvider>(context, listen: false)
                .addToCart(widget.product, quantity: _quantity);
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text('${widget.product.name} x$_quantity đã thêm vào giỏ'),
                duration: Duration(seconds: 2),
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                backgroundColor: Colors.green[600],
              ),
            );
          },
          icon: Icon(Icons.add_shopping_cart, color: Colors.white),
          label: Text('Thêm vào giỏ', style: TextStyle(color: Colors.white)),
          backgroundColor: Colors.blue[600],
          elevation: 8,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        ),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
    );
  }

  String _formatTimestamp(String timestamp) {
    final dateTime = DateTime.parse(timestamp);
    final now = DateTime.now();
    final difference = now.difference(dateTime);
    if (difference.inMinutes < 60) {
      return '${difference.inMinutes} phút trước';
    } else if (difference.inHours < 24) {
      return '${difference.inHours} giờ trước';
    } else {
      return '${difference.inDays} ngày trước';
    }
  }

  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }
}