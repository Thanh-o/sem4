import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';
import 'package:minishop/screens/order_detail_screen.dart';
import 'package:intl/intl.dart';

class HistoryScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final cartProvider = Provider.of<CartProvider>(context);
    final orders = cartProvider.orders;

    return Scaffold(
      appBar: AppBar(
        title: Text('Lịch sử đơn hàng'),
      ),
      body: orders.isEmpty
          ? Center(child: Text('Chưa có đơn hàng nào 😢'))
          : ListView.builder(
        padding: EdgeInsets.all(12),
        itemCount: orders.length,
        itemBuilder: (context, index) {
          final order = orders[index];
          final formattedDate = DateFormat('dd/MM/yyyy HH:mm').format(order.dateTime);

          return Card(
            margin: EdgeInsets.symmetric(vertical: 8),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            elevation: 3,
            child: ListTile(
              onTap: () {
                // Navigate to OrderDetailScreen when tapped
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => OrderDetailScreen(order: order),
                  ),
                );
              },
              title: Text(
                'Đơn hàng #${order.id}',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              subtitle: Text(
                'Ngày: $formattedDate\nTổng: \$${order.totalAmount.toStringAsFixed(2)}',
                style: TextStyle(color: Colors.grey[700]),
              ),
              trailing: Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey[600]),
            ),
          );
        },
      ),
    );
  }
}