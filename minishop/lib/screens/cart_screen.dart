import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/cart_provider.dart';
import 'package:minishop/screens/product_detail_screen.dart';

class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Giỏ hàng')),
      body: Consumer<CartProvider>(
        builder: (context, cart, child) {
          if (cart.items.isEmpty) {
            return Center(child: Text('Giỏ hàng của bạn đang trống 😢'));
          }

          return Column(
            children: [
              Expanded(
                child: ListView.builder(
                  itemCount: cart.items.length,
                  itemBuilder: (context, index) {
                    final item = cart.items[index];
                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      child: Card(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        elevation: 3,
                        child: ListTile(
                          onTap: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => ProductDetailScreen(product: item.product),
                              ),
                            );
                          },
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
                            '\$${item.product.price.toStringAsFixed(2)}',
                            style: TextStyle(color: Colors.green[700]),
                          ),
                          trailing: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  IconButton(
                                    icon: Icon(Icons.remove_circle_outline),
                                    onPressed: item.quantity > 1
                                        ? () => cart.updateQuantity(
                                        item.product.id, item.quantity - 1)
                                        : null,
                                  ),
                                  Text('${item.quantity}', style: TextStyle(fontSize: 16)),
                                  IconButton(
                                    icon: Icon(Icons.add_circle_outline),
                                    onPressed: () => cart.updateQuantity(
                                        item.product.id, item.quantity + 1),
                                  ),
                                  IconButton(
                                    icon: Icon(Icons.delete, color: Colors.redAccent),
                                    onPressed: () =>
                                        cart.removeFromCart(item.product.id),
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
                    SizedBox(height: 12),
                    SizedBox(
                      height: 48,
                      child: ElevatedButton.icon(
                        icon: Icon(Icons.payment),
                        label: Text('Tiến hành thanh toán'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.blueAccent,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        onPressed: () => Navigator.pushNamed(context, '/checkout'),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
