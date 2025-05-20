class Product {
  final int id;
  final String name;
  final double price;
  final String image;
  final String description; // Thêm thuộc tính này

  Product({
    required this.id,
    required this.name,
    required this.price,
    required this.image,
    required this.description, // Thêm trong constructor
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    return Product(
      id: json['id'],
      name: json['name'],
      price: json['price'].toDouble(),
      image: json['image'],
      description: json['description'] ?? '', // Đọc từ JSON (có thể fallback nếu null)
    );
  }
}
