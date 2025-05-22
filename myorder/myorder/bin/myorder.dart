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
    <td>$item</td>
    <td>$itemName</td>
    <td>${price.toStringAsFixed(2)}</td>
    <td>$currency</td>
    <td>$quantity</td>
    <td>
      <form method="POST" action="/delete" onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng này?');">
        <input type="hidden" name="item" value="$item">
        <button type="submit" class="delete-btn">Xóa</button>
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
    body {
      font-family: 'Segoe UI', Arial, sans-serif;
      background: #f0f2f5;
      margin: 0;
      padding: 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
    }
    .container {
      max-width: 1200px;
      width: 100%;
      background: #ffffff;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      padding: 24px;
    }
    h1 {
      color: #1a73e8;
      text-align: center;
      margin-bottom: 24px;
      font-size: 28px;
      font-weight: 600;
    }
    .search-form, .add-form {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 24px;
    }
    .search-form label, .add-form label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color: #333;
    }
    input[type="text"], input[type="number"] {
      width: 100%;
      padding: 10px;
      margin-bottom: 12px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 14px;
      box-sizing: border-box;
    }
    input[type="submit"], .delete-btn {
      background: #1a73e8;
      color: white;
      border: none;
      padding: 10px 16px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      transition: background 0.3s;
    }
    input[type="submit"]:hover, .delete-btn:hover {
      background: #1557b0;
    }
    .delete-btn {
      background: #dc3545;
    }
    .delete-btn:hover {
      background: #c82333;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
      background: #ffffff;
      border-radius: 8px;
      overflow: hidden;
    }
    th, td {
      padding: 12px 16px;
      text-align: left;
      border-bottom: 1px solid #eee;
    }
    th {
      background: #f8f9fa;
      color: #333;
      font-weight: 600;
      font-size: 14px;
    }
    td {
      font-size: 14px;
      color: #444;
    }
    tr:hover {
      background: #f1f3f5;
    }
    @media (max-width: 600px) {
      .container {
        padding: 16px;
      }
      h1 {
        font-size: 24px;
      }
      input[type="text"], input[type="number"], input[type="submit"], .delete-btn {
        font-size: 12px;
        padding: 8px;
      }
      th, td {
        font-size: 12px;
        padding: 10px;
      }
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>My Order</h1>

    <form class="search-form" method="GET">
      <label for="search">Search by Item Name:</label>
      <input type="text" name="search" id="search" value="$keyword" placeholder="Enter item name">
      <input type="submit" value="Search">
    </form>

    <form class="add-form" method="POST">
      <h3>Add New Order</h3>
      <input type="text" name="item" placeholder="Item ID" required>
      <input type="text" name="itemName" placeholder="Item Name" required>
      <input type="number" name="price" placeholder="Price" step="0.01" required>
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
  </div>
</body>
</html>
''';
}

Future<void> handleRequest(HttpRequest request) async {
  final orders = loadOrders();

  if (request.method == 'POST') {
    if (request.uri.path == '/delete') {
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