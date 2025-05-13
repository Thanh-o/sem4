import 'dart:io';
import 'package:sqlite3/sqlite3.dart';

//  Mở file database 'notes.db'
final db = sqlite3.open('notes.db');

void main(List<String> args) {
  // tạo db
  db.execute('''
   CREATE TABLE IF NOT EXISTS notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )''');
  // MENU
  while (true) {
    print('''
            ***************************
            1. Tạo note mới
            2. Hiển thị các notes
            3. Cập nhật note
            4. Xóa note
            5. Thoát
            ***************************
            Chọn thao tác(1-5):

''');
    final choice = stdin.readLineSync();
    switch (choice) {
      case '1':
        createNote();
        break;

      case '2':
        listNotes();
        break;

      case '3':
        updateNotes();
        break;

      case '4':
        deleteNote();
        break;

      case '5':
        db.dispose();
        exit(0);

      default:
        print('Lựa chọn không hợp lệ, hãy thử lại.');
    }
  }
}

//Tạo note mới
void createNote() {
  stdout.write('Tiêu đề:');
  final title = stdin.readLineSync() ?? '';
  stdout.write('Nội dung: ');
  final content = stdin.readLineSync() ?? '';

  final stmt = db.prepare('INSERT INTO notes (title, content)VALUES(?,?)');
  stmt.execute([title, content]);
  stmt.dispose();

  print('Đã tạo note thành công');
}

// Hiển thị ds notes
void listNotes() {
  final rows = db.select('SELECT id, title, content FROM notes');
  if (rows.isEmpty) {
    print('Chưa có note nào');
    return;
  }

  print('\n====== Danh sách Notes=====');
  for (final row in rows) {
    print('Id: ${row['id']}');
    print('Tiêu đề: ${row['title']}');
    print('Nội dung: ${row['content']}');
  }
}

// Cập nhật note theo id
void updateNotes() {
  print('Nhập ID ghi chú cần cập nhật:');
  int id = int.parse(stdin.readLineSync() ?? '0');

  // Kiểm tra xem ghi chú có tồn tại không
  final result = db.select('SELECT * FROM notes WHERE id = ?', [id]);
  if (result.isEmpty) {
    print('Ghi chú không tồn tại.');
    return;
  }

  // Nhập tiêu đề và nội dung mới từ người dùng
  print('Nhập tiêu đề mới:');
  String newTitle = stdin.readLineSync() ?? '';

  print('Nhập nội dung mới:');
  String newContent = stdin.readLineSync() ?? '';

  // Cập nhật ghi chú trong bảng
  db.execute(
    '''
        UPDATE notes
        SET title = ?, content = ?
        WHERE id = ?
    ''',
    [newTitle, newContent, id],
  );

  print('Ghi chú đã được cập nhật thành công!');
  print('ID: $id');
  print('Tiêu đề mới: $newTitle');
  print('Nội dung mới: $newContent');
  print('Thời gian cập nhật: ${DateTime.now()}');
  print('-----------------------------------');
}

// Xóa note theo id
void deleteNote() {
  print('Nhập ID ghi chú cần xóa:');
  int id = int.parse(stdin.readLineSync() ?? '0');

  // Kiểm tra xem ghi chú có tồn tại không
  final result = db.select('SELECT * FROM notes WHERE id = ?', [id]);
  if (result.isEmpty) {
    print('Ghi chú không tồn tại.');
    return;
  }

  // Xóa ghi chú khỏi bảng
  db.execute('DELETE FROM notes WHERE id = ?', [id]);

  print('Ghi chú đã được xóa thành công!');
  print('ID: $id');
  print('-----------------------------------');
  // ignore: collection_methods_unrelated_type
  print(
    // ignore: collection_methods_unrelated_type
    'Tổng số ghi chú còn lại: ${db.select('SELECT COUNT(*) FROM notes').first[0]}',
  );
  print('-----------------------------------');
  print('Nhập ID ghi chú để xem chi tiết hoặc nhấn Enter để quay lại:');
  // In danh sách ghi chú sau khi xóa
  db.select('SELECT * FROM notes');
  print('Danh sách ghi chú:');
  for (var row in result) {
    print('ID: ${row['id']}');
    print('Tiêu đề: ${row['title']}');
    print('Nội dung: ${row['content']}');
    print('Thời gian tạo: ${row['created_at']}');
    print('-----------------------------------');
  }
  if (result.isEmpty) {
    print('Không có ghi chú nào trong cơ sở dữ liệu.');
  }
  print('-----------------------------------');
}
