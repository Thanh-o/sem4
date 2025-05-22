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

  String toHtmlRow([int index = 0]) {
    return '''
<tr>
  <td>${index + 1}</td>
  <td>${item}</td>
  <td>${itemName}</td>
  <td>${quantity}</td>
  <td>${price.toStringAsFixed(2)}</td>
  <td>${currency}</td>
  <td>
    <form method="POST" action="/delete" enctype="application/x-www-form-urlencoded" onsubmit="return confirm('Bạn có chắc muốn xóa đơn hàng này?');">

      <input type="hidden" name="item" value="$item">
<button type="submit" style="background: none; border: none; font-size: 16px; cursor: pointer; color: blue;" title="Xoá đơn hàng">
  🗑
</button>

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

  final rows = filtered.asMap().entries.map((entry) => entry.value.toHtmlRow(entry.key)).join();


  return '''
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>My Order</title>
  <style>
    body {
      font-family: 'Arial', sans-serif;
      background-color: #fdfdfd;
      margin: 0;
      padding: 0;
    }
    h1 {
      color: #d87c13;
      text-align: center;
      margin-top: 30px;
    }
    form {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      gap: 10px;
      margin: 20px auto;
      max-width: 800px;
    }
    form input[type=text],
    form input[type=number] {
      padding: 10px;     
      border-radius: 5px;
    }
    form input[type=submit] {
      padding: 10px 20px;
      background-color: #d87c13;
      border: none;
      color: white;
      cursor: pointer;
      border-radius: 5px;
    }

    table {
      width: 90%;
      margin: 0 auto 30px;
      border-collapse: collapse;
      background-color: white;

      overflow: hidden;
 
    }
    th {
      background-color: #c6532d;
      color: white;
      padding: 12px;
      text-align: left;
    }
    td {
      padding: 10px;

    }
    td form {
      margin: 0;
    }
html, body {
  height: 100%;
}

body {
  display: flex;
  flex-direction: column;
}

.footer {
  margin-top: auto;
  background-color: #c6532d;
  color: white;
  text-align: center;
  padding: 12px;
  font-weight: bold;
  
}
.add-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 800px;
  margin: 0 auto 20px;
}

.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.form-group {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 300px;
  margin-right:50px;
}

.form-group2 {
  display: flex;
  flex-direction: column;
  
      min-width: 400px;
}
.form-group3 {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 150px;
}

.form-group label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #555;
}

.button-group {
  justify-content: flex-end;
  align-items: flex-end;
  display: flex;
}

.form-group input[type="text"],
.form-group input[type="number"] {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group2 label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #555;
}

.form-group2 input[type="text"],
.form-group2 input[type="number"] {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group3 label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #555;
}

.form-group3 input[type="text"],
.form-group3 input[type="number"] {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.button-group input[type="submit"] {
  background-color: #c6532d;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 5px;
  cursor: pointer;
}

  </style>
</head>
<body>
  <h1>My Order</h1>

  <form method="GET">
    <input type="text" name="search" id="search" value="$keyword" placeholder="Search by Item Name">
    <input type="submit" value="Search">
  </form>

<form method="POST" class="add-form">
  <div class="form-row">
    <div class="form-group">
      <label>Item</label>
      <input type="text" name="item" placeholder="Item" required>
    </div>
    <div class="form-group2">
      <label>Item Name</label>
      <input type="text" name="itemName" placeholder="Item Name" required>
    </div>
  </div>

  <div class="form-row">
    <div class="form-group">
      <label>Price</label>
      <input type="number" name="price" placeholder="Price" required>
    </div>
    <div class="form-group3">
      <label>Quantity</label>
      <input type="number" name="quantity" placeholder="Quantity" required>
    </div>
    <div class="form-group3">
      <label>Currency</label>
      <input type="text" name="currency" placeholder="Currency" required value="USD">
    </div>

  </div>
      <div class="form-group button-group">
      <input type="submit" value="Add Item to Cart">
    </div>
</form>


  <table>
    <tr>
      <th>Id</th>
      <th>Item</th>
      <th>Item Name</th>
      <th>Quantity</th>
      <th>Price</th>
      <th>Currency</th>
      <th>Action</th>
    </tr>
    $rows
  </table>

  <div class="footer">Số 8, Tôn Thất Thuyết, Cầu Giấy, Hà Nội</div>
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