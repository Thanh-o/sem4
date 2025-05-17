import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget {
  final List<String> categories = [
    'Điện thoại',
    'Laptop',
    'Thời trang',
    'Phụ kiện',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('MiniShop'),
        centerTitle: true,
        backgroundColor: Colors.blueAccent,
      ),
      body: Padding(
        padding: EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.shopping_bag,
              size: 100,
              color: Colors.blueAccent,
            ),
            SizedBox(height: 32),
            Text(
              'Chào mừng đến MiniShop!',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 16),
            Text(
              'Khám phá các sản phẩm tuyệt vời và thêm vào giỏ hàng của bạn.',
              style: TextStyle(fontSize: 16, color: Colors.grey[700]),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 32),

            /// Categories section
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                'Danh mục nổi bật:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
              ),
            ),
            SizedBox(height: 12),
            Wrap(
              spacing: 12,
              runSpacing: 12,
              children: categories.map((category) {
                return ActionChip(
                  label: Text(category),
                  labelStyle: TextStyle(fontWeight: FontWeight.w500),
                  backgroundColor: Colors.grey[200],
                  elevation: 2,
                  onPressed: () {
                    // Tạm thời chỉ hiện snackbar, sau này có thể điều hướng tới danh sách lọc theo category
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Đã chọn danh mục: $category')),
                    );
                  },
                );
              }).toList(),
            ),

            SizedBox(height: 40),
            ElevatedButton.icon(
              icon: Icon(Icons.store),
              label: Text('Xem danh sách sản phẩm'),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                textStyle: TextStyle(fontSize: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                backgroundColor: Colors.blueAccent,
              ),
              onPressed: () => Navigator.pushNamed(context, '/products'),
            ),
          ],
        ),
      ),
    );
  }
}
