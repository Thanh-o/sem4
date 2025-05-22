import 'dart:io';
import 'dart:convert';

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

  Map<String, dynamic> toJson() => {
    'Item': item,
    'ItemName': itemName,
    'Price': price,
    'Currency': currency,
    'Quantity': quantity,
  };

  String toHtmlRow() {
    return '''
  <tr>
    <td>${item}</td>
    <td>${itemName}</td>
    <td>${price.toStringAsFixed(2)}</td>
    <td>${currency}</td>
    <td>${quantity}</td>
    <td>
      <form method="POST" action="/delete" onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng này?');">
        <input type="hidden" name="item" value="$item">
        <input type="submit" value="Xóa" style="background-color: red; color: white; border: none; padding: 4px 8px; cursor: pointer;">
      </form>
    </td>
  </tr>
  ''';
  }

}

const String filePath = 'order.json';

List<Order> loadOrders() {
  final file = File(filePath);
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
  final data = jsonDecode(file.readAsStringSync());
  return List<Order>.from(data.map((o) => Order.fromJson(o)));
}

void saveOrders(List<Order> orders) {
  final file = File(filePath);
  file.writeAsStringSync(jsonEncode(orders.map((o) => o.toJson()).toList()));
}

String renderHtml(List<Order> orders, [String keyword = '']) {
  final filtered = keyword.isEmpty
      ? orders
      : orders
      .where((o) => o.itemName.toLowerCase().contains(keyword.toLowerCase()))
      .toList();

  final rows = filtered.map((o) => o.toHtmlRow()).join();

  return '''
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Order Manager</title>
  <style>
    body { font-family: Arial; background: #f4f4f4; padding: 20px; }
    h1 { color: #333; }
    form, table { background: white; padding: 16px; margin-top: 20px; border-radius: 8px; }
    input[type=text], input[type=number] { width: 100%; padding: 8px; margin: 4px 0; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
    th { background: #eee; }
    input[type=submit] { background: #007BFF; color: white; border: none; padding: 10px; }
  </style>
</head>
<body>
  <h1>My Order</h1>

  <form method="GET">
    <label for="search">Search by ItemName:</label>
    <input type="text" name="search" id="search" value="$keyword">
    <input type="submit" value="Search">
  </form>

  <form method="POST">
    <h3>Add New Order</h3>
    <input type="text" name="item" placeholder="Item" required>
    <input type="text" name="itemName" placeholder="Item Name" required>
    <input type="number" name="price" placeholder="Price"  required>
    <input type="text" name="currency" placeholder="Currency" required>
    <input type="number" name="quantity" placeholder="Quantity" required>
    <input type="submit" value="Add Order">
  </form>

<table>
  <tr>
    <th>Item</th>
    <th>Item Name</th>
    <th>Price</th>
    <th>Currency</th>
    <th>Quantity</th>
    <th>Action</th>
  </tr>
  $rows
</table>

</body>
</html>
''';
}

Future<void> handleRequest(HttpRequest request) async {
  final orders = loadOrders();

  if (request.method == 'POST') {
    if (request.uri.path == '/delete') {
      // Xử lý xóa đơn hàng theo item
      final content = await utf8.decoder.bind(request).join();
      final params = Uri.splitQueryString(content);
      final itemToDelete = params['item'];

      if (itemToDelete != null) {
        orders.removeWhere((o) => o.item == itemToDelete);
        saveOrders(orders);
      }

      request.response
        ..statusCode = HttpStatus.found
        ..headers.set('Location', '/')
        ..close();
      return;
    }

    // Xử lý POST thêm mới
    final content = await utf8.decoder.bind(request).join();
    final params = Uri.splitQueryString(content);

    final newOrder = Order(
      item: params['item']!,
      itemName: params['itemName']!,
      price: double.tryParse(params['price']!) ?? 0,
      currency: params['currency']!,
      quantity: int.tryParse(params['quantity']!) ?? 1,
    );

    orders.add(newOrder);
    saveOrders(orders);

    request.response
      ..statusCode = HttpStatus.found
      ..headers.set('Location', '/')
      ..close();
  } else if (request.method == 'GET') {
    final keyword = request.uri.queryParameters['search'] ?? '';
    final html = renderHtml(orders, keyword);

    request.response
      ..headers.contentType = ContentType.html
      ..write(html)
      ..close();
  } else {
    request.response
      ..statusCode = HttpStatus.methodNotAllowed
      ..write('Method not allowed')
      ..close();
  }
}



void main() async {
  final server = await HttpServer.bind(InternetAddress.loopbackIPv4, 8080);
  print('✅ Web server đang chạy tại: http://localhost:8080');
  await for (HttpRequest request in server) {
    handleRequest(request);
  }
}
