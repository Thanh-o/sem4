import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:minishop/providers/product_provider.dart';
import 'package:minishop/providers/cart_provider.dart';

class ProductListScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final productProvider = Provider.of<ProductProvider>(context);
    if (productProvider.products.isEmpty) {
      // Truyền context vào loadProducts
      productProvider.loadProducts(context);
    }

    return Scaffold(
      appBar: AppBar(
        title: Text('Sản phẩm'),
        actions: [
          IconButton(
            icon: Icon(Icons.shopping_cart),
            onPressed: () => Navigator.pushNamed(context, '/cart'),
          ),
        ],
      ),
      body: Consumer<ProductProvider>(
        builder: (context, provider, child) {
          if (provider.products.isEmpty) {
            return Center(child: CircularProgressIndicator());
          }
          return GridView.builder(
            padding: EdgeInsets.all(8),
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              childAspectRatio: 0.7,
              crossAxisSpacing: 8,
              mainAxisSpacing: 8,
            ),
            itemCount: provider.products.length,
            itemBuilder: (context, index) {
              final product = provider.products[index];
              return Card(
                child: Column(
                  children: [
                    Expanded(
                      child: Image.network(
                        product.image,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) =>
                            Icon(Icons.broken_image),
                      ),
                    ),
                    Padding(
                      padding: EdgeInsets.all(8),
                      child: Column(
                        children: [
                          Text(product.name, style: TextStyle(fontWeight: FontWeight.bold)),
                          Text('\$${product.price.toStringAsFixed(2)}'),
                          ElevatedButton(
                            onPressed: () {
                              Provider.of<CartProvider>(context, listen: false)
                                  .addToCart(product);
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('${product.name} đã thêm vào giỏ')),
                              );
                            },
                            child: Text('Thêm vào giỏ'),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              );
            },
          );
        },
      ),
    );
  }
}