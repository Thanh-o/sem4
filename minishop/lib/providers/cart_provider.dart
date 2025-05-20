import 'package:flutter/material.dart';
import 'package:minishop/models/cart_item.dart';
import 'package:minishop/models/product.dart';

class Order {
  final String id;
  final List<CartItem> items;
  final double totalAmount;
  final String paymentMethod;
  final DateTime dateTime;

  Order({
    required this.id,
    required this.items,
    required this.totalAmount,
    required this.paymentMethod,
    required this.dateTime,
  });
}

class CartProvider with ChangeNotifier {
  List<CartItem> _items = [];
  List<Order> _orders = [];

  List<CartItem> get items => _items;
  List<Order> get orders => _orders;

  int get itemCount => _items.fold(0, (sum, item) => sum + item.quantity);

  double get totalPrice =>
      _items.fold(0, (sum, item) => sum + item.product.price * item.quantity);

  void addToCart(Product product, {int quantity = 1}) {
    // Kiểm tra số lượng hợp lệ
    if (quantity <= 0) {
      return; // Không thêm nếu số lượng không hợp lệ
    }

    // Tìm sản phẩm trong giỏ
    final existingItemIndex = _items.indexWhere(
          (item) => item.product.id == product.id,
    );

    if (existingItemIndex >= 0) {
      // Nếu sản phẩm đã có, tăng số lượng
      _items[existingItemIndex].quantity += quantity;
    } else {
      // Nếu sản phẩm chưa có, thêm mới
      _items.add(CartItem(product: product, quantity: quantity));
    }
    notifyListeners();
  }

  void removeFromCart(int productId) {
    _items.removeWhere((item) => item.product.id == productId);
    notifyListeners();
  }

  void updateQuantity(int productId, int quantity) {
    final itemIndex = _items.indexWhere((item) => item.product.id == productId);
    if (itemIndex >= 0) {
      if (quantity > 0) {
        _items[itemIndex].quantity = quantity;
      } else {
        removeFromCart(productId);
      }
      notifyListeners();
    }
  }

  void clearCart() {
    _items.clear();
    notifyListeners();
  }

  void addOrder(List<CartItem> items, double totalAmount, String paymentMethod) {
    _orders.add(Order(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      items: List.from(items),
      totalAmount: totalAmount,
      paymentMethod: paymentMethod,
      dateTime: DateTime.now(),
    ));
    notifyListeners();
  }
}