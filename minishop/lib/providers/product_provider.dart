import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:minishop/models/product.dart';
import 'package:flutter/material.dart';

class ProductProvider with ChangeNotifier {
  List<Product> _products = [];
  bool _isLoaded = false;

  List<Product> get products => _products;

  Future<void> loadProducts(BuildContext context) async {
    if (_isLoaded) return;
    final String response = await DefaultAssetBundle.of(context)
        .loadString('assets/products.json');
    final List<dynamic> data = jsonDecode(response);
    _products = data.map((json) => Product.fromJson(json)).toList();
    _isLoaded = true;
    notifyListeners();
  }
}