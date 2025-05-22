import 'dart:convert';
import 'dart:io';

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

  Map<String, dynamic> toJson() {
    return {
      'Item': item,
      'ItemName': itemName,
      'Price': price,
      'Currency': currency,
      'Quantity': quantity,
    };
  }

  @override
  String toString() {
    return '$item | $itemName | $price $currency | Qty: $quantity';
  }
}

const String jsonFilePath = 'order.json';

void saveOrdersToFile(List<Order> orders) {
  final List<Map<String, dynamic>> jsonList =
  orders.map((order) => order.toJson()).toList();
  final jsonString = jsonEncode(jsonList);
  File(jsonFilePath).writeAsStringSync(jsonString);

}

List<Order> loadOrdersFromFile() {
  final file = File(jsonFilePath);
  if (file.existsSync()) {
    final content = file.readAsStringSync();
    final List<dynamic> jsonList = jsonDecode(content);
    return jsonList.map((e) => Order.fromJson(e)).toList();
  } else {
    // Nếu file chưa tồn tại, khởi tạo với dữ liệu mặc định
    const initialJson = '''
    [
      {"Item": "A1000","ItemName": "Iphone 15","Price": 1200,"Currency":"USD","Quantity":1},
      {"Item": "A1001","ItemName": "Iphone 16","Price": 1500,"Currency":"USD","Quantity":1}
    ]
    ''';
    final List<dynamic> jsonList = jsonDecode(initialJson);
    final orders = jsonList.map((e) => Order.fromJson(e)).toList();
    saveOrdersToFile(orders); // Ghi file lần đầu
    return orders;
  }
}

void main() {
  List<Order> orders = loadOrdersFromFile();

  while (true) {
    print('\n==== ORDER MENU ====');
    print('1. Hiển thị tất cả đơn hàng');
    print('2. Thêm đơn hàng mới');
    print('3. Tìm kiếm theo tên sản phẩm');
    print('0. Thoát');
    stdout.write('Chọn chức năng: ');
    String? choice = stdin.readLineSync();

    switch (choice) {
      case '1':
        print('\n--- Danh sách đơn hàng ---');
        for (var order in orders) {
          print(order);
        }
        break;

      case '2':
        print('\n--- Thêm đơn hàng ---');
        stdout.write('Item: ');
        String item = stdin.readLineSync()!;
        stdout.write('ItemName: ');
        String itemName = stdin.readLineSync()!;
        stdout.write('Price: ');
        double price = double.parse(stdin.readLineSync()!);
        stdout.write('Currency: ');
        String currency = stdin.readLineSync()!;
        stdout.write('Quantity: ');
        int quantity = int.parse(stdin.readLineSync()!);

        Order newOrder = Order(
          item: item,
          itemName: itemName,
          price: price,
          currency: currency,
          quantity: quantity,
        );

        orders.add(newOrder);
        saveOrdersToFile(orders);
        print('✅ Đã thêm và lưu đơn hàng vào order.json!');
        break;

      case '3':
        stdout.write('\nNhập từ khóa tìm kiếm: ');
        String keyword = stdin.readLineSync()!.toLowerCase();
        List<Order> results = orders
            .where((o) => o.itemName.toLowerCase().contains(keyword))
            .toList();
        if (results.isEmpty) {
          print('❌ Không tìm thấy đơn hàng nào.');
        } else {
          print('🔍 Kết quả tìm kiếm:');
          for (var o in results) {
            print(o);
          }
        }
        break;

      case '0':
        print('👋 Tạm biệt!');
        return;

      default:
        print('⚠️ Lựa chọn không hợp lệ!');
    }
  }
}
