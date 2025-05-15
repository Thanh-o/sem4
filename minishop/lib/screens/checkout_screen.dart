import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';

class CheckoutScreen extends StatelessWidget {
  // Hàm hiển thị Dialog xác nhận thanh toán
  Future<void> _showConfirmDialog(BuildContext context, CartProvider cart) async {
    return showDialog<void>(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: Text('Xác nhận thanh toán'),
          content: Text('Bạn có chắc chắn muốn thanh toán không?'),
          actions: <Widget>[
            TextButton(
              child: Text('Hủy'),
              onPressed: () {
                Navigator.of(dialogContext).pop(); // Đóng Dialog
              },
            ),
            TextButton(
              child: Text('Xác nhận'),
              onPressed: () {
                Navigator.of(dialogContext).pop(); // Đóng Dialog
                // Thực hiện thanh toán
                cart.clearCart();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Thanh toán thành công!')),
                );
                // Quay về màn hình chính
                Navigator.popUntil(context, (route) => route.isFirst);
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartProvider>(context);
    return Scaffold(
      appBar: AppBar(title: Text('Thanh toán')),
      body: cart.items.isEmpty
          ? Center(child: Text('Không có sản phẩm để thanh toán'))
          : Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: cart.items.length,
              itemBuilder: (context, index) {
                final item = cart.items[index];
                return ListTile(
                  leading: Image.network(
                    item.product.image,
                    width: 50,
                    height: 50,
                    errorBuilder: (context, error, stackTrace) =>
                        Icon(Icons.broken_image),
                  ),
                  title: Text(item.product.name),
                  subtitle: Text(
                    '\$${item.product.price.toStringAsFixed(2)} x ${item.quantity}',
                  ),
                );
              },
            ),
          ),
          Padding(
            padding: EdgeInsets.all(16),
            child: Column(
              children: [
                Text(
                  'Tổng cộng: \$${cart.totalPrice.toStringAsFixed(2)}',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () {
                    // Hiển thị Dialog xác nhận trước khi thanh toán
                    _showConfirmDialog(context, cart);
                  },
                  child: Text('Xác nhận thanh toán'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}