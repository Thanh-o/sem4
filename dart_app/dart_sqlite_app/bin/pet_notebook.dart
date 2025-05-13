import 'dart:io';
import 'package:sqlite3/sqlite3.dart';

//=======================//
//   PHẦN 1: OOP         //
//=======================//

abstract class Pet {
  String name;
  int age;

  Pet(this.name, this.age);

  void makeSound();

  @override
  String toString() => 'Tên: $name | Tuổi: $age';
}

class Dog extends Pet {
  Dog(String name, int age) : super(name, age);

  @override
  void makeSound() => print('Gâu gâu!');
}

class Cat extends Pet {
  Cat(String name, int age) : super(name, age);

  @override
  void makeSound() => print('Meo meo!');
}

void printPetInfo(Pet pet) {
  print(pet);
  pet.makeSound();
}

//=============================//
//   PHẦN 2: LƯU TRỮ SQLITE    //
//=============================//

final db = sqlite3.open('notes.db');

void main(List<String> args) {
  // Tạo bảng nếu chưa tồn tại
  db.execute('''
    CREATE TABLE IF NOT EXISTS pets (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      type TEXT NOT NULL,
      name TEXT NOT NULL,
      age INTEGER NOT NULL
    );
  ''');

  while (true) {
    print('''
===== MENU SỔ TAY THÚ CƯNG =====
1. Thêm thú cưng
2. Hiển thị danh sách
3. In thông tin 1 thú cưng
4. Cập nhật thú cưng
5. Xóa thú cưng
6. Thoát
===============================
Chọn (1-6):
''');
    final choice = stdin.readLineSync();

    switch (choice) {
      case '1':
        insertPet();
        break;
      case '2':
        listPets();
        break;
      case '3':
        showPetInfo();
        break;
      case '4':
        updatePet();
        break;
      case '5':
        deletePet();
        break;
      case '6':
        db.dispose();
        exit(0);
      default:
        print('Lựa chọn không hợp lệ!');
    }
  }
}

void insertPet() {
  stdout.write('Nhập tên thú cưng: ');
  final name = stdin.readLineSync() ?? '';
  stdout.write('Nhập tuổi: ');
  final age = int.tryParse(stdin.readLineSync() ?? '') ?? 0;
  stdout.write('Loại thú cưng (dog/cat): ');
  final type = stdin.readLineSync()?.toLowerCase();

  if (type != 'dog' && type != 'cat') {
    print('Chỉ hỗ trợ "dog" hoặc "cat"!');
    return;
  }

  db.execute('INSERT INTO pets (type, name, age) VALUES (?, ?, ?)', [
    type,
    name,
    age,
  ]);
  print('✔️ Đã thêm thú cưng!');
}

void listPets() {
  final result = db.select('SELECT * FROM pets');
  if (result.isEmpty) {
    print('Không có thú cưng nào!');
    return;
  }

  print('📋 Danh sách thú cưng:');
  for (final row in result) {
    print(
      'ID: ${row['id']} | Tên: ${row['name']} | Tuổi: ${row['age']} | Loại: ${row['type']}',
    );
  }
}

void showPetInfo() {
  stdout.write('Nhập ID thú cưng: ');
  final id = int.tryParse(stdin.readLineSync() ?? '') ?? -1;

  final row = db.select('SELECT * FROM pets WHERE id = ?', [id]);
  if (row.isEmpty) {
    print('❌ Không tìm thấy thú cưng!');
    return;
  }

  final type = row.first['type'] as String;
  final name = row.first['name'] as String;
  final age = row.first['age'] as int;

  Pet pet;
  if (type == 'dog') {
    pet = Dog(name, age);
  } else if (type == 'cat') {
    pet = Cat(name, age);
  } else {
    print('❌ Loại thú cưng không hợp lệ!');
    return;
  }

  printPetInfo(pet);
}

void updatePet() {
  stdout.write('Nhập ID thú cưng cần cập nhật: ');
  final id = int.tryParse(stdin.readLineSync() ?? '') ?? -1;

  final row = db.select('SELECT * FROM pets WHERE id = ?', [id]);
  if (row.isEmpty) {
    print('❌ Không tìm thấy thú cưng!');
    return;
  }

  stdout.write('Tên mới: ');
  final name = stdin.readLineSync() ?? '';
  stdout.write('Tuổi mới: ');
  final age = int.tryParse(stdin.readLineSync() ?? '') ?? 0;
  stdout.write('Loại mới (dog/cat): ');
  final type = stdin.readLineSync()?.toLowerCase();

  if (type != 'dog' && type != 'cat') {
    print('❌ Loại không hợp lệ!');
    return;
  }

  db.execute('UPDATE pets SET name = ?, age = ?, type = ? WHERE id = ?', [
    name,
    age,
    type,
    id,
  ]);
  print('✔️ Cập nhật thành công!');
}

void deletePet() {
  stdout.write('Nhập ID thú cưng cần xóa: ');
  final id = int.tryParse(stdin.readLineSync() ?? '') ?? -1;

  final row = db.select('SELECT * FROM pets WHERE id = ?', [id]);
  if (row.isEmpty) {
    print('❌ Không tìm thấy thú cưng!');
    return;
  }

  db.execute('DELETE FROM pets WHERE id = ?', [id]);
  print('🗑️ Đã xóa thú cưng!');
}
