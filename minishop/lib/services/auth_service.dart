import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService {
  final storage = FlutterSecureStorage();
  final String baseUrl = 'https://682bf191d29df7a95be4ecee.mockapi.io/users'; // đổi thành của bạn

  Future<bool> register(String email, String password) async {
    final response = await http.post(
      Uri.parse(baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'email': email, 'password': password}),
    );
    return response.statusCode == 201;
  }

  Future<bool> login(String email, String password) async {
    final response = await http.get(
      Uri.parse('$baseUrl?email=$email&password=$password'),
    );

    if (response.statusCode == 200) {
      final users = jsonDecode(response.body);
      if (users.isNotEmpty) {
        final userId = users[0]['id'];
        await storage.write(key: 'mock_token', value: userId); // giả lập token
        return true;
      }
    }
    return false;
  }

  Future<String?> getToken() async {
    return await storage.read(key: 'mock_token');
  }

  Future<void> logout() async {
    await storage.delete(key: 'mock_token');
  }
}
