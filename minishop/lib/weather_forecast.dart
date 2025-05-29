import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_fonts/google_fonts.dart';

// 🧊 Model thời tiết
class Weather {
  final String dateTime;
  final double temp;
  final String description;
  final String icon;
  final double humidity;
  final double windSpeed;

  Weather({
    required this.dateTime,
    required this.temp,
    required this.description,
    required this.icon,
    required this.humidity,
    required this.windSpeed,
  });

  factory Weather.fromJson(Map<String, dynamic> json) {
    return Weather(
      dateTime: json['dt_txt'],
      temp: json['main']['temp'].toDouble(),
      description: json['weather'][0]['description'],
      icon: json['weather'][0]['icon'],
      humidity: json['main']['humidity'].toDouble(),
      windSpeed: json['wind']['speed'].toDouble(),
    );
  }
}

// 🌐 Gọi API
Future<List<Weather>> fetchWeather(String city) async {
  const apiKey = '8d7715ecb6e05b33bc4656cb0ea95087';
  final url =
      'https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$apiKey&units=metric&lang=vi';

  try {
    final response = await http.get(Uri.parse(url));
    if (response.statusCode == 200) {
      final List weatherList = jsonDecode(response.body)['list'];
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('weather_$city', response.body);
      return weatherList.map((json) => Weather.fromJson(json)).toList();
    } else if (response.statusCode == 404) {
      throw Exception('Không tìm thấy thành phố: $city');
    } else {
      throw Exception('Lỗi lấy dữ liệu: ${response.statusCode}');
    }
  } catch (e) {
    final prefs = await SharedPreferences.getInstance();
    final cachedData = prefs.getString('weather_$city');
    if (cachedData != null) {
      final List weatherList = jsonDecode(cachedData)['list'];
      return weatherList.map((json) => Weather.fromJson(json)).toList();
    }
    throw e;
  }
}

BoxDecoration neumorphicDecoration() {
  return BoxDecoration(
    color: Color(0xFFF0F0F3),
    borderRadius: BorderRadius.circular(12),
    boxShadow: [
      BoxShadow(
        color: Colors.grey[400]!,
        offset: Offset(4, 4),
        blurRadius: 8,
        spreadRadius: 1,
      ),
      BoxShadow(
        color: Colors.white,
        offset: Offset(-4, -4),
        blurRadius: 8,
        spreadRadius: 1,
      ),
    ],
  );
}

void main() {
  runApp(WeatherApp());
}

class WeatherApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Dự báo thời tiết',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        textTheme: GoogleFonts.interTextTheme(
          Theme.of(context).textTheme,
        ),
        scaffoldBackgroundColor: Color(0xFFF0F0F3),
      ),
      home: WeatherScreen(),
    );
  }
}

class WeatherScreen extends StatefulWidget {
  @override
  _WeatherScreenState createState() => _WeatherScreenState();
}

class _WeatherScreenState extends State<WeatherScreen> {
  late Future<List<Weather>> _weatherFuture;
  final TextEditingController _cityController = TextEditingController();
  String selectedCity = 'Hanoi';
  bool isCelsius = true;

  @override
  void initState() {
    super.initState();
    _cityController.text = selectedCity;
    _weatherFuture = fetchWeather(selectedCity);
  }

  void _onCitySearch(String city) {
    if (city.isNotEmpty) {
      setState(() {
        selectedCity = city;
        _weatherFuture = fetchWeather(selectedCity);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFF0F0F3),
      body: SafeArea(
        child: FutureBuilder<List<Weather>>(
          future: _weatherFuture,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final weatherList = snapshot.data!;
              final currentWeather = weatherList[0];
              final temp = isCelsius
                  ? currentWeather.temp
                  : (currentWeather.temp * 9 / 5) + 32;
              final groupedWeather = _groupWeatherByDay(weatherList);

              return SingleChildScrollView(
                child: Column(
                  children: [
                    // Thanh tìm kiếm
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Container(
                        decoration: neumorphicDecoration(),
                        child: TextField(
                          controller: _cityController,
                          style: TextStyle(color: Colors.black87),
                          decoration: InputDecoration(
                            hintText: 'Nhập tên thành phố', // Đã sửa
                            hintStyle: TextStyle(color: Colors.grey[500]),
                            prefixIcon: Icon(Icons.search, color: Colors.grey[600]),
                            suffixIcon: IconButton(
                              icon: Icon(Icons.clear, color: Colors.grey[600]),
                              onPressed: () {
                                _cityController.clear();
                              },
                            ),
                            border: InputBorder.none,
                            contentPadding: EdgeInsets.symmetric(vertical: 15),
                          ),
                          onSubmitted: _onCitySearch,
                        ),
                      ),
                    ),
                    // Thời tiết hiện tại
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 20),
                      child: Column(
                        children: [
                          Text(
                            selectedCity,
                            style: TextStyle(
                              fontSize: 28,
                              fontWeight: FontWeight.w600,
                              color: Colors.black87,
                            ),
                          ),
                          SizedBox(height: 10),
                          Image.network(
                            'http://openweathermap.org/img/wn/${currentWeather.icon}@4x.png',
                            width: 120,
                          ),
                          Text(
                            '${temp.toStringAsFixed(1)} ${isCelsius ? '°C' : '°F'}',
                            style: TextStyle(
                              fontSize: 48,
                              fontWeight: FontWeight.w300,
                              color: Colors.black87,
                            ),
                          ),
                          Text(
                            currentWeather.description,
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.grey[600],
                            ),
                          ),
                          SizedBox(height: 10),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text('💧 Độ ẩm: ${currentWeather.humidity}%'),
                              SizedBox(width: 20),
                              Text('💨 Gió: ${currentWeather.windSpeed} m/s'),
                            ],
                          ),
                        ],
                      ),
                    ),
                    // Chuyển đổi đơn vị
                    Container(
                      decoration: neumorphicDecoration(),
                      padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      margin: EdgeInsets.symmetric(horizontal: 16),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text('°C', style: TextStyle(color: Colors.black87)),
                          Switch(
                            value: !isCelsius,
                            onChanged: (value) {
                              setState(() {
                                isCelsius = !value;
                              });
                            },
                            activeColor: Colors.blue[300],
                            inactiveThumbColor: Colors.grey[400],
                          ),
                          Text('°F', style: TextStyle(color: Colors.black87)),
                        ],
                      ),
                    ),
                    SizedBox(height: 20),
                    // Dự báo theo ngày (cuộn ngang)
                    Container(
                      height: 200,
                      child: ListView.builder(
                        scrollDirection: Axis.horizontal,
                        padding: EdgeInsets.symmetric(horizontal: 16),
                        itemCount: groupedWeather.length,
                        itemBuilder: (context, index) {
                          final day = groupedWeather.keys.elementAt(index);
                          final dayWeather = groupedWeather[day]![0];
                          final temp = isCelsius
                              ? dayWeather.temp
                              : (dayWeather.temp * 9 / 5) + 32;
                          return Container(
                            width: 140,
                            margin: EdgeInsets.only(right: 16),
                            decoration: neumorphicDecoration(),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Text(
                                  DateFormat('EEE, dd/MM').format(DateTime.parse(day)),
                                  style: TextStyle(
                                    fontWeight: FontWeight.w600,
                                    fontSize: 16,
                                  ),
                                ),
                                SizedBox(height: 10),
                                Image.network(
                                  'http://openweathermap.org/img/wn/${dayWeather.icon}@2x.png',
                                  width: 50,
                                ),
                                Text(
                                  '${temp.toStringAsFixed(1)} ${isCelsius ? '°C' : '°F'}',
                                  style: TextStyle(fontSize: 18),
                                ),
                                Text(
                                  dayWeather.description,
                                  style: TextStyle(color: Colors.grey[600]),
                                  textAlign: TextAlign.center,
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                  ],
                ),
              );
            } else if (snapshot.hasError) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.error, color: Colors.red, size: 40),
                    SizedBox(height: 8),
                    Text(
                      'Lỗi: ${snapshot.error}',
                      style: TextStyle(color: Colors.black87),
                    ),
                  ],
                ),
              );
            }
            return Center(child: CircularProgressIndicator());
          },
        ),
      ),
    );
  }

  Map<String, List<Weather>> _groupWeatherByDay(List<Weather> weatherList) {
    final Map<String, List<Weather>> grouped = {};
    for (var weather in weatherList) {
      final date = weather.dateTime.split(' ')[0];
      if (!grouped.containsKey(date)) {
        grouped[date] = [];
      }
      grouped[date]!.add(weather);
    }
    return grouped;
  }

  @override
  void dispose() {
    _cityController.dispose();
    super.dispose();
  }
}