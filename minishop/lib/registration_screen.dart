import 'package:flutter/material.dart';

class RegistrationScreen extends StatefulWidget {
  @override
  _RegistrationScreenState createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen> {
  final _formKey = GlobalKey<FormState>();
  String? _firstName, _surname, _day, _month, _year, _gender, _mobileEmail, _password;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create a new account')),
      body: Form(
        key: _formKey,
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              TextFormField(
                key: const Key('firstName'),
                decoration: const InputDecoration(labelText: 'First Name'),
                validator: (value) => value!.isEmpty ? 'Required' : null,
                onChanged: (value) => _firstName = value,
              ),
              TextFormField(
                key: const Key('surname'),
                decoration: const InputDecoration(labelText: 'Surname'),
                validator: (value) => value!.isEmpty ? 'Required' : null,
                onChanged: (value) => _surname = value,
              ),
              TextFormField(
                key: const Key('day'),
                decoration: const InputDecoration(labelText: 'Day'),
                validator: (value) => value!.isEmpty ? 'Required' : null,
                onChanged: (value) => _day = value,
              ),
              TextFormField(
                key: const Key('month'),
                decoration: const InputDecoration(labelText: 'Month'),
                validator: (value) => value!.isEmpty ? 'Required' : null,
                onChanged: (value) => _month = value,
              ),
              TextFormField(
                key: const Key('year'),
                decoration: const InputDecoration(labelText: 'Year'),
                validator: (value) {
                  if (value!.isEmpty) return 'Required';
                  final year = int.tryParse(value);
                  if (year == null || year > 2012) return 'Must be at least 13 years old';
                  return null;
                },
                onChanged: (value) => _year = value,
              ),
              DropdownButtonFormField<String>(
                key: const Key('gender'),
                decoration: const InputDecoration(labelText: 'Gender'),
                items: ['Female', 'Male', 'Custom'].map((gender) {
                  return DropdownMenuItem(value: gender, child: Text(gender));
                }).toList(),
                validator: (value) => value == null ? 'Required' : null,
                onChanged: (value) => setState(() => _gender = value),
              ),
              TextFormField(
                key: const Key('mobileEmail'),
                decoration: const InputDecoration(labelText: 'Mobile or Email'),
                validator: (value) => value!.isEmpty ? 'Required' : null,
                onChanged: (value) => _mobileEmail = value,
              ),
              TextFormField(
                key: const Key('password'),
                decoration: const InputDecoration(labelText: 'Password'),
                validator: (value) =>
                value!.length < 8 ? 'Password must be at least 8 characters' : null,
                onChanged: (value) => _password = value,
                obscureText: true,
              ),
              ElevatedButton(
                key: const Key('signUpButton'),
                onPressed: () {
                  if (_formKey.currentState!.validate()) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Account created successfully!')),
                    );
                  }
                },
                child: const Text('Sign Up'),
              ),
              TextButton(
                key: const Key('alreadyHaveAccount'),
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Redirecting to login...')),
                  );
                },
                child: const Text('Already have an account?'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}