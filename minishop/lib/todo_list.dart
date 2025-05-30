import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

void main() {
  runApp(const ToDoApp());
}

class ToDoApp extends StatelessWidget {
  const ToDoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'To-Do List',
      theme: ThemeData(
        primaryColor: Colors.teal,
        colorScheme: ColorScheme.fromSwatch(
          primarySwatch: Colors.teal,
          accentColor: Colors.amber,
        ),
        textTheme: const TextTheme(
          bodyMedium: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
        ),
        // cardTheme: const CardTheme(
        //   elevation: 4,
        //   margin: EdgeInsets.symmetric(vertical: 4, horizontal: 8),
        //   shape: RoundedRectangleBorder(
        //     borderRadius: BorderRadius.all(Radius.circular(12)),
        //   ),
        // ),
      ),
      home: const ToDoListScreen(),
    );
  }
}

class Task {
  String title;
  bool isCompleted;

  Task({required this.title, this.isCompleted = false});

  Map<String, dynamic> toJson() => {
    'title': title,
    'isCompleted': isCompleted,
  };

  factory Task.fromJson(Map<String, dynamic> json) => Task(
    title: json['title'],
    isCompleted: json['isCompleted'],
  );
}

class ToDoListScreen extends StatefulWidget {
  const ToDoListScreen({super.key});

  @override
  State<ToDoListScreen> createState() => _ToDoListScreenState();
}

class _ToDoListScreenState extends State<ToDoListScreen> {
  final List<Task> _tasks = [];
  final TextEditingController _controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadTasks();
  }

  Future<void> _loadTasks() async {
    final prefs = await SharedPreferences.getInstance();
    final String? tasksString = prefs.getString('tasks');
    if (tasksString != null) {
      final List<dynamic> tasksJson = jsonDecode(tasksString);
      setState(() {
        _tasks.addAll(tasksJson.map((json) => Task.fromJson(json)).toList());
      });
    }
  }

  Future<void> _saveTasks() async {
    final prefs = await SharedPreferences.getInstance();
    final String tasksString = jsonEncode(_tasks.map((task) => task.toJson()).toList());
    await prefs.setString('tasks', tasksString);
  }

  void _addTask() {
    if (_controller.text.isNotEmpty) {
      setState(() {
        _tasks.add(Task(title: _controller.text));
        _controller.clear();
      });
      _saveTasks();
    }
  }

  void _toggleTask(int index) {
    setState(() {
      _tasks[index].isCompleted = !_tasks[index].isCompleted;
    });
    _saveTasks();
  }

  void _deleteTask(int index) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xóa công việc'),
        content: Text('Bạn có muốn xóa "${_tasks[index].title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Hủy'),
          ),
          TextButton(
            onPressed: () {
              setState(() {
                _tasks.removeAt(index);
              });
              _saveTasks();
              Navigator.pop(context);
            },
            child: const Text('Xóa', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('To-Do List'),
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Colors.teal, Colors.tealAccent],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            // Input field
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: 'Nhập công việc mới',
                border: const OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                ),
                filled: true,
                fillColor: Colors.grey[100],
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () => _controller.clear(),
                ),
              ),
              onSubmitted: (_) => _addTask(),
            ),
            const SizedBox(height: 16),
            // Task list with animation
            Expanded(
              child: _tasks.isEmpty
                  ? const Center(child: Text('Chưa có công việc nào!'))
                  : ListView.builder(
                itemCount: _tasks.length,
                itemBuilder: (context, index) {
                  return AnimatedOpacity(
                    opacity: _tasks[index].isCompleted ? 0.6 : 1.0,
                    duration: const Duration(milliseconds: 300),
                    child: Card(
                      child: ListTile(
                        leading: Checkbox(
                          value: _tasks[index].isCompleted,
                          onChanged: (_) => _toggleTask(index),
                          activeColor: Colors.teal,
                        ),
                        title: Text(
                          _tasks[index].title,
                          style: TextStyle(
                            decoration: _tasks[index].isCompleted
                                ? TextDecoration.lineThrough
                                : null,
                            color: _tasks[index].isCompleted
                                ? Colors.grey
                                : Colors.black87,
                          ),
                        ),
                        trailing: IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          onPressed: () => _deleteTask(index),
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _addTask,
        backgroundColor: Colors.teal,
        child: const Icon(Icons.add),
      ),
    );
  }
}