import 'dart:convert';
import 'dart:io';

// Lớp Order
class Order {
  String item;
  String itemName;
  double price;
  String currency;
  int quantity;

  Order({
    required this.item,
    required this.itemName,
    required this.price,
    required this.currency,
    required this.quantity,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      item: json['Item'],
      itemName: json['ItemName'],
      price: json['Price'].toDouble(),
      currency: json['Currency'],
      quantity: json['Quantity'],
    );
  }

  String toHtmlRow() {
    return '''
    <tr>
      <td>${item}</td>
      <td>${itemName}</td>
      <td>${price.toStringAsFixed(2)}</td>
      <td>$currency</td>
      <td>$quantity</td>
    </tr>
    ''';
  }
}

// Đường dẫn đến file JSON
const String jsonFilePath = 'order.json';

// Đọc danh sách Order từ file
List<Order> loadOrders() {
  final file = File(jsonFilePath);
  if (!file.existsSync()) {
    file.writeAsStringSync(jsonEncode([
      {
        "Item": "A1000",
        "ItemName": "Iphone 15",
        "Price": 1200,
        "Currency": "USD",
        "Quantity": 1
      },
      {
        "Item": "A1001",
        "ItemName": "Iphone 16",
        "Price": 1500,
        "Currency": "USD",
        "Quantity": 1
      }
    ]));
  }

  final content = file.readAsStringSync();
  final List<dynamic> jsonList = jsonDecode(content);
  return jsonList.map((e) => Order.fromJson(e)).toList();
}

// Tạo HTML để hiển thị danh sách
String generateHtml(List<Order> orders) {
  final rows = orders.map((o) => o.toHtmlRow()).join();
  return '''
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Order List</title>
  <style>
    body { font-family: Arial; padding: 20px; background-color: #f2f2f2; }
    h1 { color: #333; }
    table { border-collapse: collapse; width: 100%; background: #fff; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #4CAF50; color: white; }
    tr:hover { background-color: #f5f5f5; }
  </style>
</head>
<body>
  <h1>Order List</h1>
  <table>
    <tr>
      <th>Item</th>
      <th>Item Name</th>
      <th>Price</th>
      <th>Currency</th>
      <th>Quantity</th>
    </tr>
    $rows
  </table>
</body>
</html>
''';
}

void main() async {
  final server = await HttpServer.bind(InternetAddress.loopbackIPv4, 8080);
  print('✅ Web server đang chạy tại: http://localhost:8080');

  await for (HttpRequest request in server) {
    if (request.method == 'GET' && request.uri.path == '/') {
      final orders = loadOrders();
      final html = generateHtml(orders);

      request.response
        ..headers.contentType = ContentType.html
        ..write(html)
        ..close();
    } else {
      request.response
        ..statusCode = HttpStatus.notFound
        ..write('404 Not Found')
        ..close();
    }
  }
}
