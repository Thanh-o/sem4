import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';

class CheckoutScreen extends StatefulWidget {
  @override
  State<CheckoutScreen> createState() => _CheckoutScreenState();
}

class _CheckoutScreenState extends State<CheckoutScreen> {
  String selectedMethod = 'Tiền mặt';

  Future<void> _showConfirmDialog(BuildContext context, CartProvider cart) async {
    return showDialog<void>(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: Text('Xác nhận thanh toán', style: TextStyle(fontWeight: FontWeight.bold)),
          content: Text('Bạn có chắc chắn muốn thanh toán đơn hàng này bằng $selectedMethod không?'),
          actions: <Widget>[
            TextButton(
              child: Text('Hủy'),
              onPressed: () => Navigator.of(dialogContext).pop(),
            ),
            ElevatedButton(
              child: Text('Xác nhận'),
              onPressed: () {
                Navigator.of(dialogContext).pop();
                cart.clearCart();
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Thanh toán thành công bằng $selectedMethod!')),
                );
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
          ? Center(child: Text('Không có sản phẩm để thanh toán 😢'))
          : Column(
        children: [
          Expanded(
            child: ListView.separated(
              itemCount: cart.items.length,
              separatorBuilder: (_, __) => Divider(height: 1),
              itemBuilder: (context, index) {
                final item = cart.items[index];
                return Card(
                  margin: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  elevation: 2,
                  child: ListTile(
                    leading: ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.network(
                        item.product.image,
                        width: 60,
                        height: 60,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) =>
                            Icon(Icons.broken_image, size: 40),
                      ),
                    ),
                    title: Text(item.product.name,
                        style: TextStyle(fontWeight: FontWeight.w600)),
                    subtitle: Text(
                      '\$${item.product.price.toStringAsFixed(2)} x ${item.quantity}',
                      style: TextStyle(color: Colors.grey[700]),
                    ),
                  ),
                );
              },
            ),
          ),
          Container(
            decoration: BoxDecoration(
              color: Colors.grey[100],
              borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
              boxShadow: [
                BoxShadow(color: Colors.black12, blurRadius: 8, offset: Offset(0, -2)),
              ],
            ),
            padding: EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Tổng cộng: \$${cart.totalPrice.toStringAsFixed(2)}',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.right,
                ),
                SizedBox(height: 16),
                Text('Chọn phương thức thanh toán:', style: TextStyle(fontWeight: FontWeight.w500)),
                RadioListTile<String>(
                  value: 'Tiền mặt',
                  groupValue: selectedMethod,
                  title: Text('💵 Tiền mặt'),
                  onChanged: (value) {
                    setState(() {
                      selectedMethod = value!;
                    });
                  },
                ),
                RadioListTile<String>(
                  value: 'Thẻ tín dụng',
                  groupValue: selectedMethod,
                  title: Text('💳 Thẻ tín dụng'),
                  onChanged: (value) {
                    setState(() {
                      selectedMethod = value!;
                    });
                  },
                ),
                RadioListTile<String>(
                  value: 'Chuyển khoản',
                  groupValue: selectedMethod,
                  title: Text('🏦 Chuyển khoản'),
                  onChanged: (value) {
                    setState(() {
                      selectedMethod = value!;
                    });
                  },
                ),
                SizedBox(height: 12),
                SizedBox(
                  height: 48,
                  child: ElevatedButton.icon(
                    icon: Icon(Icons.check_circle_outline),
                    label: Text('Xác nhận thanh toán'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.blueAccent,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    onPressed: () => _showConfirmDialog(context, cart),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
