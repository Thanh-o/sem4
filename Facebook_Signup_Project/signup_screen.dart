import 'package:flutter/material.dart';

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
        fontFamily: 'Roboto', // Font gần giống với Facebook
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
  bool _firstNameTouched = false;
  bool _surnameTouched = false;

  String? _day;
  String? _month;
  String? _year;
  String? _gender;
  String? _mobileEmail;
  String? _password;
  bool _dobTouched = false;
  bool _mobileEmailTouched = false;
  bool _passwordTouched = false;
  bool get _dobHasError {
    if (!_dobTouched) return false;
    if (_day == null || _month == null || _year == null) return true;
    final year = int.tryParse(_year ?? '');
    final currentYear = DateTime.now().year;
    if (year == null || currentYear - year < 13) return true;
    return false;
  }
  final FocusNode _firstNameFocusNode = FocusNode();

  // Danh sách cho Dropdown
  final List<String> days = List.generate(31, (index) => (index + 1).toString());
  final List<String> months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  final List<String> years = List.generate(126, (index) => (2025 - index).toString());
  @override
  void initState() {
    super.initState();

    _firstNameFocusNode.addListener(() {
      if (!_firstNameFocusNode.hasFocus) {
        // Khi focus bị mất (blur), kiểm tra và vẽ lại form
        setState(() {});
      }
    });
  }

  @override
  void dispose() {
    _firstNameFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white, // Màu nền của Facebook
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 20.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Logo
              Text(
                'facebook',
                style: TextStyle(
                  fontSize: 50,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF1877F2),
                  letterSpacing: -1.5,
                ),
              ),
              SizedBox(height: 20),

              // Tiêu đề
              Text(
                'Create a new account',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              Text(
                "It's quick and easy.",
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.grey[600],
                  decoration: TextDecoration.underline,
                ),
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
                    // First name và Surname
                    Row(
                      children: [
                        Expanded(
                          child: TextFormField(
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
                              errorBorder: OutlineInputBorder(
                                borderSide: BorderSide(color: Colors.red),
                                borderRadius: BorderRadius.circular(5),
                              ),
                              focusedErrorBorder: OutlineInputBorder(
                                borderSide: BorderSide(color: Colors.red),
                                borderRadius: BorderRadius.circular(5),
                              ),
                              suffixIcon: (_firstNameTouched && (_firstName ?? '').isEmpty)
                                  ? Padding(
                                padding: EdgeInsets.all(15),
                                child: CircleAvatar(
                                  backgroundColor: Colors.red,
                                  radius: 10,
                                  child: Text(
                                    '!',
                                    style: TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.bold,
                                      fontSize: 15,
                                    ),
                                  ),
                                ),
                              )
                                  : null,



                            ),
                            onChanged: (value) {
                              setState(() {
                                _firstName = value;
                                _firstNameTouched = true;
                              });
                            },

                            validator: (value) {
                              if (value == null || value.isEmpty) {
                                return '';
                              }
                              return null;
                            },
                            onSaved: (value) => _firstName = value,
                          ),
                        ),
                        SizedBox(width: 8),
                        Expanded(
                          child: TextFormField(
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
                              errorBorder: OutlineInputBorder(
                                borderSide: BorderSide(color: Colors.red),
                                borderRadius: BorderRadius.circular(5),
                              ),
                              focusedErrorBorder: OutlineInputBorder(
                                borderSide: BorderSide(color: Colors.red),
                                borderRadius: BorderRadius.circular(5),
                              ),
                              suffixIcon: (_surnameTouched && (_surname ?? '').isEmpty)
                                  ? Padding(
                                padding: EdgeInsets.all(15),
                                child: CircleAvatar(
                                  backgroundColor: Colors.red,
                                  radius: 10,
                                  child: Text(
                                    '!',
                                    style: TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.bold,
                                      fontSize: 15,
                                    ),
                                  ),
                                ),
                              )
                                  : null,


                            ),
                            onChanged: (value) {
                              setState(() {
                                _surname = value;
                                _surnameTouched = true;
                              });
                            },

                            validator: (value) {
                              if (value == null || value.isEmpty) {
                                return '';
                              }
                              return null;
                            },
                            onSaved: (value) => _surname = value,
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
                          crossAxisAlignment: CrossAxisAlignment.center,
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
                            if (_day == null || _month == null || _year == null ||
                                (int.tryParse(_year ?? '') != null &&
                                    DateTime.now().year - int.parse(_year!) < 13))
                              Container(
                                width: 20,
                                height: 20,
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  shape: BoxShape.circle,
                                ),
                                alignment: Alignment.center,
                                child: Text(
                                  '!',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                          ],
                        ),

                        SizedBox(height: 8),

                        Row(
                          children: [
                            Expanded(
                              child: DropdownButtonFormField<String>(
                                
                                decoration: InputDecoration(
                                  hintText: 'Day',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: days.map((day) => DropdownMenuItem(
                                  value: day,
                                  child: Text(day),
                                )).toList(),
                                onChanged: (value) => setState(() => _day = value),
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
                                decoration: InputDecoration(
                                  hintText: 'Month',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: months.map((month) => DropdownMenuItem(
                                  value: month,
                                  child: Text(month.substring(0, 3)),
                                )).toList(),
                                onChanged: (value) => setState(() => _month = value),
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
                                decoration: InputDecoration(
                                  hintText: 'Year',
                                  filled: true,
                                  fillColor: Colors.white,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                ),
                                items: years.map((year) => DropdownMenuItem(
                                  value: year,
                                  child: Text(year),
                                )).toList(),
                                onChanged: (value) => setState(() => _year = value),
                                value: _year,
                                validator: (value) {
                                  if (value == null || value.isEmpty) {
                                    return '';
                                  }
                                  int year = int.parse(value);
                                  int currentYear = DateTime.now().year;
                                  if (currentYear - year < 13) {
                                    return 'Must be at least 13 years old.';
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ),
                      ],
                    )
                    ,

                    SizedBox(height: 10),

                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
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
                            if (_gender == null)
                              Container(
                                width: 20,
                                height: 20,
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  shape: BoxShape.circle,
                                ),
                                alignment: Alignment.center,
                                child: Text(
                                  '!',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 14,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                          ],
                        ),

                        SizedBox(height: 8),

                        // Gender options
                        Row(
                          children: [
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() => _gender = 'Female'),
                                child: Container(
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Female',
                                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                                      ),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Female',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() => _gender = value),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() => _gender = 'Male'),
                                child: Container(
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Male',
                                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Male',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() => _gender = value),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                            SizedBox(width: 8),
                            Expanded(
                              child: InkWell(
                                onTap: () => setState(() => _gender = 'Custom'),
                                child: Container(
                                  padding: EdgeInsets.symmetric(vertical: 2, horizontal: 3),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    border: Border.all(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(5),
                                  ),
                                  child: Row(
                                    children: [
                                      Text('Custom',
                                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),),
                                      Spacer(),
                                      Radio<String>(
                                        value: 'Custom',
                                        groupValue: _gender,
                                        onChanged: (value) => setState(() => _gender = value),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ],
                    )
                    ,

                    SizedBox(height: 10),

                    // Mobile number or email
            Column(
              children: [
                // Mobile number or email
                Focus(
                  onFocusChange: (hasFocus) {
                    if (!hasFocus) {
                      setState(() {
                        _mobileEmailTouched = true;
                      });
                    }
                  },
                  child: Stack(
                    children: [
                      TextFormField(
                        decoration: InputDecoration(
                          hintText: 'Mobile number or email',
                          filled: true,
                          fillColor: Colors.white,
                          contentPadding: EdgeInsets.only(right: 40, top: 15, bottom: 15, left: 12),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5),
                            borderSide: BorderSide(
                              color: (_mobileEmailTouched && (_mobileEmail == null || _mobileEmail!.isEmpty))
                                  ? Colors.red
                                  : Colors.grey,
                            ),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5),
                            borderSide: BorderSide(
                              color: (_mobileEmailTouched && (_mobileEmail == null || _mobileEmail!.isEmpty))
                                  ? Colors.red
                                  : Colors.blue,
                              width: 2,
                            ),
                          ),
                          errorText: null,
                          errorStyle: TextStyle(height: 0, fontSize: 0),
                        ),
                        validator: (_) => null,
                        onChanged: (value) {
                          setState(() {
                            _mobileEmail = value;
                          });
                        },
                        onSaved: (value) => _mobileEmail = value,
                      ),

                      if (_mobileEmailTouched && (_mobileEmail == null || _mobileEmail!.isEmpty))
                        Positioned(
                          right: 12,
                          top: 0,
                          bottom: 0,
                          child: Container(
                            width: 20,
                            height: 20,
                            decoration: BoxDecoration(
                              color: Colors.red,
                              shape: BoxShape.circle,
                            ),
                            alignment: Alignment.center,
                            child: Text(
                              '!',
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          ),
                        ),
                    ],
                  ),
                ),

                SizedBox(height: 10),

                // New password
                Focus(
                  onFocusChange: (hasFocus) {
                    if (!hasFocus) {
                      setState(() {
                        _passwordTouched = true;
                      });
                    }
                  },
                  child: Stack(
                    children: [
                      TextFormField(
                        decoration: InputDecoration(
                          hintText: 'New password',
                          filled: true,
                          fillColor: Colors.white,
                          contentPadding: EdgeInsets.only(right: 40, top: 15, bottom: 15, left: 12),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5),
                            borderSide: BorderSide(
                              color: (_passwordTouched && (_password == null || _password!.isEmpty || _password!.length < 8))
                                  ? Colors.red
                                  : Colors.grey,
                            ),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(5),
                            borderSide: BorderSide(
                              color: (_passwordTouched && (_password == null || _password!.isEmpty || _password!.length < 8))
                                  ? Colors.red
                                  : Colors.blue,
                              width: 2,
                            ),
                          ),
                          errorText: null,
                          errorStyle: TextStyle(height: 0, fontSize: 0),
                        ),
                        obscureText: true,
                        validator: (_) => null,
                        onChanged: (value) => setState(() => _password = value),
                        onSaved: (value) => _password = value,
                      ),

                      if (_passwordTouched && (_password == null || _password!.isEmpty || _password!.length < 8))
                        Positioned(
                          right: 12,
                          top: 0,
                          bottom: 0,
                          child: Container(
                            width: 20,
                            height: 20,
                            decoration: BoxDecoration(
                              color: Colors.red,
                              shape: BoxShape.circle,
                            ),
                            alignment: Alignment.center,
                            child: Text(
                              '!',
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                          ),
                        ),
                    ],
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
                        textAlign: TextAlign.center,
                        text: TextSpan(
                          text: 'By clicking Sign Up, you agree to our ',
                          style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                          children: [
                            TextSpan(
                              text: 'Terms, Privacy Policy',
                              style: TextStyle(color: Color(0xFF1877F2)),
                            ),
                            TextSpan(text: ' and '),
                            TextSpan(
                              text: 'Cookies Policy.',
                              style: TextStyle(color: Color(0xFF1877F2)),
                            ),
                            TextSpan(text: ' You may receive SMS notifications from us and can opt out at any time.'),
                          ],
                        ),
                      ),
                    ),
                    SizedBox(height: 20),

                    // Sign Up button
                    ElevatedButton(
                      onPressed: () {
                        if (_formKey.currentState!.validate()) {
                          _formKey.currentState!.save();
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Account created successfully!')),
                          );
                        }
                      },
                      child: Text(
                        'Sign Up',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFF00FF00),
                        padding: EdgeInsets.symmetric(vertical: 8, horizontal: 60), // tăng padding ngang để rộng vừa đủ
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(5),
                        ),
                      ),
                    ),


                    SizedBox(height: 10),

                    // Already have an account?
                    Center(
                      child: TextButton(
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