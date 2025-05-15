import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';

class CheckoutScreen extends StatelessWidget {
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
                    cart.clearCart();
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Thanh toán thành công!')),
                    );
                    Navigator.popUntil(context, (route) => route.isFirst);
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