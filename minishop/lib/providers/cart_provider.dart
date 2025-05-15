import 'package:flutter/material.dart';
import 'package:minishop/models/cart_item.dart';
import 'package:minishop/models/product.dart';

class CartProvider with ChangeNotifier {
  List<CartItem> _items = [];

  List<CartItem> get items => _items;

  double get totalPrice =>
      _items.fold(0, (sum, item) => sum + item.product.price * item.quantity);

  void addToCart(Product product) {
    final existingItem = _items.firstWhere(
          (item) => item.product.id == product.id,
      orElse: () => CartItem(product: product),
    );
    if (_items.contains(existingItem)) {
      existingItem.quantity++;
    } else {
      _items.add(existingItem);
    }
    notifyListeners();
  }

  void removeFromCart(int productId) {
    _items.removeWhere((item) => item.product.id == productId);
    notifyListeners();
  }

  void updateQuantity(int productId, int quantity) {
    final item = _items.firstWhere((item) => item.product.id == productId);
    if (quantity > 0) {
      item.quantity = quantity;
    } else {
      removeFromCart(productId);
    }
    notifyListeners();
  }

  void clearCart() {
    _items.clear();
    notifyListeners();
  }
}