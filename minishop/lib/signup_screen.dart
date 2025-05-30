import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart'; // For TC44 (link handling)
import 'package:shared_preferences/shared_preferences.dart';
void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Đăng ký Facebook',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        fontFamily: 'Roboto',
      ),
      home: DangKyManHinh(),
    );
  }
}

class DangKyManHinh extends StatefulWidget {
  @override
  _DangKyManHinhState createState() => _DangKyManHinhState();
}

class _DangKyManHinhState extends State<DangKyManHinh> {
  final _formKey = GlobalKey<FormState>();
  String? _firstName;
  String? _surname;
  String? _day;
  String? _month;
  String? _year;
  String? _gender;
  String? _mobileEmail;
  String? _password;
  bool _firstNameTouched = false;
  bool _surnameTouched = false;
  bool _dobTouched = false;
  bool _genderTouched = false;
  bool _mobileEmailTouched = false;
  bool _passwordTouched = false;

  // Focus nodes for all fields to handle focus events (TC23, TC34)
  final FocusNode _firstNameFocusNode = FocusNode();
  final FocusNode _surnameFocusNode = FocusNode();
  final FocusNode _mobileEmailFocusNode = FocusNode();
  final FocusNode _passwordFocusNode = FocusNode();

  // Lists for Dropdown
  final List<String> days = List.generate(31, (index) => (index + 1).toString());
  final List<String> months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  final List<String> years = List.generate(
      DateTime.now().year - 1900 + 1, (index) => (DateTime.now().year - index).toString());

  // Date validation for TC30, TC53
  bool _isValidDate() {
    if (_day == null || _month == null || _year == null) return false;
    final day = int.tryParse(_day!);
    final year = int.tryParse(_year!);
    if (day == null || year == null) return false;
    if (_month == 'February' && day > 29) return false;
    if (_month == 'February' && day == 29 && !_isLeapYear(year)) return false;
    return true;
  }

  bool _isLeapYear(int year) {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
  }

  // Phone number validation for TC37
  bool _isValidPhoneNumber(String? value) {
    if (value == null || value.isEmpty) return false;
    final phoneRegex = RegExp(r'^\+\d{1,3}\d{6,}$');
    return phoneRegex.hasMatch(value);
  }

  @override
  void initState() {
    super.initState();
    _restoreFormData();

    // Restore form data for TC52 (e.g., using SharedPreferences or similar)
    // For simplicity, we'll simulate persistence with in-memory data
    // In a real app, use SharedPreferences or a state management solution
    _firstNameFocusNode.addListener(() {
      if (!_firstNameFocusNode.hasFocus) {
        setState(() => _firstNameTouched = true);
      }
    });
    _surnameFocusNode.addListener(() {
      if (!_surnameFocusNode.hasFocus) {
        setState(() => _surnameTouched = true);
      }
    });
    _mobileEmailFocusNode.addListener(() {
      if (!_mobileEmailFocusNode.hasFocus) {
        setState(() => _mobileEmailTouched = true);
      }
    });
    _passwordFocusNode.addListener(() {
      if (!_passwordFocusNode.hasFocus) {
        setState(() => _passwordTouched = true);
      }
    });
  }
  Future<void> _restoreFormData() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _firstName = prefs.getString('firstName');
      _surname = prefs.getString('surname');
      _day = prefs.getString('day');
      _month = prefs.getString('month');
      _year = prefs.getString('year');
      _gender = prefs.getString('gender');
      _mobileEmail = prefs.getString('mobileEmail');
      _password = prefs.getString('password');
    });
  }

  void _saveFormData() async {
    final prefs = await SharedPreferences.getInstance();
    prefs.setString('firstName', _firstName ?? '');
    prefs.setString('surname', _surname ?? '');
    prefs.setString('day', _day ?? '');
    prefs.setString('month', _month ?? '');
    prefs.setString('year', _year ?? '');
    prefs.setString('gender', _gender ?? '');
    prefs.setString('mobileEmail', _mobileEmail ?? '');
    prefs.setString('password', _password ?? '');
  }
  @override
  void dispose() {
    _firstNameFocusNode.dispose();
    _surnameFocusNode.dispose();
    _mobileEmailFocusNode.dispose();
    _passwordFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Logo
              Text(
                'facebook',
                key: Key('facebookLogo'), // Added for TC01
                style: TextStyle(
                  fontSize: 50,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF1877F2),
                  letterSpacing: -1.5,
                ),
              ),
              SizedBox(height: 20),
              // Title
              Text(
                'Create a new account',
                key: Key('createAccountTitle'), // Added for TC01
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              Text(
                "It's quick and easy.",
                style: TextStyle(fontSize: 16, color: Colors.grey[600]),
              ),
              SizedBox(height: 15),
              Container(
                height: 1,
                color: Colors.grey[400],
                width: double.infinity,
              ),
              SizedBox(height: 20),
              // Form
              Form(
                key: _formKey,
                child: Column(
                  children: [
                    // First name and Surname
                    Row(
                      children: [
                        Expanded(
                          child: Stack(
                            children: [
                              TextFormField(
                                key: Key('firstName'), // Added for test script
                                focusNode: _firstNameFocusNode,
                                autovalidateMode: AutovalidateMode.onUserInteraction,
                                decoration: InputDecoration(
                                  hintText: 'First name',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.blue, width: 2),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  errorBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.red),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedErrorBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.red),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                validator: (value) {
                                  if (value == null || value.trim().isEmpty) {
                                    return '';
                                  }
                                  // TC48: Prevent HTML injection
                                  if (value.contains('<') || value.contains('>')) {
                                    return 'Invalid characters';
                                  }
                                  return null;
                                },
                                onChanged: (value) {
                                  setState(() {
                                    _firstName = value;
                                    _firstNameTouched = true;
                                  });
                                },
                                onSaved: (value) => _firstName = value,
                              ),
                              if (_firstNameTouched && (_firstName?.trim().isEmpty ?? true))
                                Positioned(
                                  right: 12,
                                  top: 0,
                                  bottom: 0,
                                  child: Container(
                                    key: Key('errorIcon'), // Added for test script
                                    width: 20,
                                    height: 20,
                                    decoration: BoxDecoration(
                                      color: Colors.red,
                                      shape: BoxShape.circle,
                                    ),
                                    child: Text(
                                      '!',
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontWeight: FontWeight.bold,
                                        fontSize: 15,
                                      ),
                                      textAlign: TextAlign.center,
                                    ),
                                  ),
                                ),
                            ],
                          ),
                        ),
                        SizedBox(width: 8),
                        Expanded(
                          child: Stack(
                            children: [
                              TextFormField(
                                key: Key('surname'), // Added for test script
                                focusNode: _surnameFocusNode,
                                autovalidateMode: AutovalidateMode.onUserInteraction,
                                decoration: InputDecoration(
                                  hintText: 'Surname',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.blue, width: 2),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  errorBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.red),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedErrorBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.red),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                validator: (value) {
                                  if (value == null || value.trim().isEmpty) {
                                    return '';
                                  }
                                  // TC48: Prevent HTML injection
                                  if (value.contains('<') || value.contains('>')) {
                                    return 'Invalid characters';
                                  }
                                  return null;
                                },
                                onChanged: (value) {
                                  setState(() {
                                    _surname = value;
                                    _surnameTouched = true;
                                  });
                                },
                                onSaved: (value) => _surname = value,
                              ),
                              if (_surnameTouched && (_surname?.trim().isEmpty ?? true))
                                Positioned(
                                  right: 12,
                                  top: 0,
                                  bottom: 0,
                                  child: Container(
                                    key: Key('errorIcon'), // Added for test script
                                    width: 20,
                                    height: 20,
                                    decoration: BoxDecoration(
                                      color: Colors.red,
                                      shape: BoxShape.circle,
                                    ),
                                    child: Text(
                                      '!',
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontWeight: FontWeight.bold,
                                        fontSize: 15,
                                      ),
                                      textAlign: TextAlign.center,
                                    ),
                                  ),
                                ),
                            ],
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 10),
                    // Date of birth
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            RichText(
                              text: TextSpan(
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w500,
                                  color: Colors.grey[600],
                                ),
                                children: [
                                  TextSpan(text: 'Date of birth '),
                                  WidgetSpan(
                                    child: Container(
                                      margin: EdgeInsets.only(left: 4),
                                      padding: EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                      decoration: BoxDecoration(
                                        color: Colors.grey[600],
                                        shape: BoxShape.circle,
                                      ),
                                      child: Text(
                                        '?',
                                        style: TextStyle(
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                          fontSize: 16,
                                        ),
                                        textAlign: TextAlign.center,

                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            Spacer(),
                            if (_dobTouched && !_isValidDate())
                              Container(
                                key: Key('errorIcon'), // Added for test script
                                width: 20,
                                height: 20,
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  shape: BoxShape.circle,
                                ),
                                child: Text(
                                  '!',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                          ],
                        ),
                        SizedBox(height: 8),
                        Row(
                          children: [
                            Expanded(
                              child: DropdownButtonFormField<String>(
                                key: Key('day'), // Added for test script
                                decoration: InputDecoration(
                                  hintText: 'Day',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.blue, width: 2),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: days.map((day) => DropdownMenuItem(
                                  value: day,
                                  child: Text(day),
                                )).toList(),
                                onChanged: (value) => setState(() {
                                  _day = value;
                                  _dobTouched = true;
                                }),
                                value: _day,
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return '';
                                  }
                                  return null;
                                },
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: DropdownButtonFormField<String>(
                                key: Key('month'), // Added for test script
                                decoration: InputDecoration(
                                  hintText: 'Month',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.blue, width: 2),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: months.map((month) => DropdownMenuItem(
                                  value: month,
                                  child: Text(month.substring(0, 3)),
                                )).toList(),
                                onChanged: (value) => setState(() {
                                  _month = value;
                                  _dobTouched = true;
                                }),
                                value: _month,
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return '';
                                  }
                                  return null;
                                },
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: DropdownButtonFormField<String>(
                                key: Key('year'), // Added for test script
                                decoration: InputDecoration(
                                  hintText: 'Year',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.blue, width: 2),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: years.map((year) => DropdownMenuItem(
                                  value: year,
                                  child: Text(year),
                                )).toList(),
                                onChanged: (value) => setState(() {
                                  _year = value;
                                  _dobTouched = true;
                                }),
                                value: _year,
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return '';
                                  }
                                  final year = int.parse(value);
                                  final currentYear = DateTime.now().year;
                                  if (year > currentYear) {
                                    return 'Year cannot be in the future'; // TC42
                                  }
                                  if (currentYear - year < 13) {
                                    return 'Must be at least 13 years old';
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(height: 10),
                    // Gender
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            RichText(
                              text: TextSpan(
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.w500,
                                  color: Colors.grey[600],
                                ),
                                children: [
                                  TextSpan(text: 'Gender '),
                                  WidgetSpan(
                                    child: Container(
                                      margin: EdgeInsets.only(left: 4),
                                      padding: EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                      decoration: BoxDecoration(
                                        color: Colors.grey[600],
                                        shape: BoxShape.circle,
                                      ),
                                      child: Text(
                                        '?',
                                        style: TextStyle(
                                          color: Colors.white,
                                          fontWeight: FontWeight.bold,
                                          fontSize: 16,
                                        ),
                                        textAlign: TextAlign.center,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            Spacer(),
                            if (_genderTouched && _gender == null)
                              Container(
                                key: Key('errorIcon'), // Added for test script
                                width: 20,
                                height: 20,
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  shape: BoxShape.circle,
                                ),
                                child: Text(
                                  '!',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                          ],
                        ),
                        SizedBox(height: 8),
                        Row(
                          children: [
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() {
                                  _gender = 'Female';
                                  _genderTouched = true;
                                }),
                                child: Container(
                                  key: Key('genderFemale'), // Added for test script
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Female', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Female',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() {
                                          _gender = value;
                                          _genderTouched = true;
                                        }),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() {
                                  _gender = 'Male';
                                  _genderTouched = true;
                                }),
                                child: Container(
                                  key: Key('genderMale'), // Added for test script
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Male', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Male',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() {
                                          _gender = value;
                                          _genderTouched = true;
                                        }),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() {
                                  _gender = 'Custom';
                                  _genderTouched = true;
                                }),
                                child: Container(
                                  key: Key('genderCustom'), // Added for test script
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Custom', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Custom',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() {
                                          _gender = value;
                                          _genderTouched = true;
                                        }),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(height: 10),
                    // Mobile number or email
                    Stack(
                      children: [
                        TextFormField(
                          key: Key('mobileEmail'), // Added for test script
                          focusNode: _mobileEmailFocusNode,
                          decoration: InputDecoration(
                            hintText: 'Mobile number or email',
                            filled: true,
                            fillColor: Colors.white,
                            contentPadding: EdgeInsets.only(right: 40, top: 15, bottom: 15, left: 12),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(5),
                              borderSide: BorderSide(color: Colors.grey),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(5),
                              borderSide: BorderSide(color: Colors.blue, width: 2),
                            ),
                            errorBorder: OutlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                              borderRadius: BorderRadius.circular(5),
                            ),
                            focusedErrorBorder: OutlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                              borderRadius: BorderRadius.circular(5),
                            ),
                          ),
                          validator: (value) {
                            if (value == null || value.trim().isEmpty) {
                              return '';
                            }
                            // TC16, TC26, TC36: Email validation
                            if (value.contains('@')) {
                              final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
                              if (!emailRegex.hasMatch(value)) {
                                return 'Invalid email format';
                              }
                            } else {
                              // TC17, TC27, TC37: Phone number validation
                              if (!_isValidPhoneNumber(value)) {
                                return 'Invalid phone number';
                              }
                            }
                            return null;
                          },
                          onChanged: (value) => setState(() => _mobileEmail = value),
                          onSaved: (value) => _mobileEmail = value,
                        ),
                        if (_mobileEmailTouched &&
                            (_mobileEmail == null ||
                                _mobileEmail!.trim().isEmpty ||
                                (!_mobileEmail!.contains('@') && !_isValidPhoneNumber(_mobileEmail)) ||
                                (_mobileEmail!.contains('@') &&
                                    !RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(_mobileEmail!))))
                          Positioned(
                            right: 12,
                            top: 0,
                            bottom: 0,
                            child: Container(
                              key: Key('errorIcon'), // Added for test script
                              width: 20,
                              height: 20,
                              decoration: BoxDecoration(
                                color: Colors.red,
                                shape: BoxShape.circle,
                              ),
                              child: Text(
                                '!',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: 15,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                      ],
                    ),
                    SizedBox(height: 10),
                    // New password
                    Stack(
                      children: [
                        TextFormField(
                          key: Key('password'), // Added for test script
                          focusNode: _passwordFocusNode,
                          decoration: InputDecoration(
                            hintText: 'New password',
                            filled: true,
                            fillColor: Colors.white,
                            contentPadding: EdgeInsets.only(right: 40, top: 15, bottom: 15, left: 12),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(5),
                              borderSide: BorderSide(color: Colors.grey),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(5),
                              borderSide: BorderSide(color: Colors.blue, width: 2),
                            ),
                            errorBorder: OutlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                              borderRadius: BorderRadius.circular(5),
                            ),
                            focusedErrorBorder: OutlineInputBorder(
                              borderSide: BorderSide(color: Colors.red),
                              borderRadius: BorderRadius.circular(5),
                            ),
                          ),
                          obscureText: true,
                          validator: (value) {
                            if (value == null || value.trim().isEmpty) {
                              return '';
                            }
                            if (value.length < 8) {
                              return 'Password must be at least 8 characters';
                            }
                            return null;
                          },
                          onChanged: (value) => setState(() => _password = value),
                          onSaved: (value) => _password = value,
                        ),
                        if (_passwordTouched &&
                            (_password == null || _password!.trim().isEmpty || _password!.length < 8))
                          Positioned(
                            right: 12,
                            top: 0,
                            bottom: 0,
                            child: Container(
                              key: Key('errorIcon'), // Added for test script
                              width: 20,
                              height: 20,
                              decoration: BoxDecoration(
                                color: Colors.red,
                                shape: BoxShape.circle,
                              ),
                              child: Text(
                                '!',
                                style: TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: 15,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                      ],
                    ),
                    SizedBox(height: 10),
                    // Terms and conditions
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: Text(
                        'People who use our service may have uploaded your contact information. Learn more.',
                        style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                        textAlign: TextAlign.center,
                      ),
                    ),
                    SizedBox(height: 8),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: RichText(
                        key: Key('termsAndPolicy'), // Added for TC18, TC55
                        textAlign: TextAlign.center,
                        text: TextSpan(
                          text: 'By clicking Sign Up, you agree to our ',
                          style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                          children: [
                            TextSpan(
                              text: 'Terms',
                              style: TextStyle(color: Color(0xFF1877F2)),
                              recognizer: TapGestureRecognizer()
                                ..onTap = () async {
                                  // TC44: Handle Terms link
                                  final url = 'https://www.facebook.com/terms';
                                  if (await canLaunch(url)) {
                                    await launch(url);
                                  }
                                },
                            ),
                            TextSpan(text: ', '),
                            TextSpan(
                              text: 'Privacy Policy',
                              style: TextStyle(color: Color(0xFF1877F2)),
                              recognizer: TapGestureRecognizer()
                                ..onTap = () async {
                                  // TC44: Handle Privacy Policy link
                                  final url = 'https://www.facebook.com/privacy';
                                  if (await canLaunch(url)) {
                                    await launch(url);
                                  }
                                },
                            ),
                            TextSpan(text: ' and '),
                            TextSpan(
                              text: 'Cookies Policy.',
                              style: TextStyle(color: Color(0xFF1877F2)),
                              recognizer: TapGestureRecognizer()
                                ..onTap = () async {
                                  // TC44: Handle Cookies Policy link
                                  final url = 'https://www.facebook.com/policies/cookies';
                                  if (await canLaunch(url)) {
                                    await launch(url);
                                  }
                                },
                            ),
                            TextSpan(text: ' You may receive SMS notifications from us and can opt out at any time.'),
                          ],
                        ),
                      ),
                    ),
                    SizedBox(height: 20),
                    // Sign Up button
                    ElevatedButton(
                      key: Key('signUpButton'), // Added for test script
                      onPressed: () {
                        if (_formKey.currentState!.validate() && _isValidDate() && _gender != null) {
                          _formKey.currentState!.save();
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Account created successfully!')),
                          );
                        } else {
                          setState(() {
                            _firstNameTouched = true;
                            _surnameTouched = true;
                            _dobTouched = true;
                            _genderTouched = true;
                            _mobileEmailTouched = true;
                            _passwordTouched = true;
                          });
                        }
                      },
                      child: Text(
                        'Sign Up',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFF00FF00),
                        padding: EdgeInsets.symmetric(vertical: 8, horizontal: 60),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(5),
                        ),
                      ),
                    ),
                    SizedBox(height: 10),
                    // Already have an account?
                    Center(
                      child: TextButton(
                        key: Key('alreadyHaveAccount'), // Added for test script
                        onPressed: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Redirecting to login...')),
                          );
                        },
                        child: Text(
                          'Already have an account?',
                          style: TextStyle(
                            fontSize: 17,
                            color: Color(0xFF1877F2),
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}