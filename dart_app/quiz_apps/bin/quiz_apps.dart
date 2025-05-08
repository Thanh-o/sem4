import 'dart:convert';
import 'dart:ffi';
import 'dart:io';
import 'dart:math';

class Question {
  final String question;
  final List<String> options;
  final int answerIndex;

  Question({
    required this.question,
    required this.options,
    required this.answerIndex,
  });

  factory Question.fromJson(Map<String, dynamic> json) {
    return Question(
      question: json['question'] as String,
      options: List<String>.from(json['options']),
      answerIndex: json['answer'] as int,
    );
  }
}

Future<List<Question>> loadQuestions(String path) async {
  final file = File(path);
  final content = await file.readAsString();
  final List<dynamic> data = jsonDecode(content);
  return data.map((e) => Question.fromJson(e)).toList();
}

void startQuiz(List<Question> questions) {
  var rng = Random();
  int score = 0;
  int total = questions.length;

  questions.shuffle(rng);

  for (var i = 0; i < questions.length; i++) {
    final q = questions[i];
    print('\nQuestion ${i + 1}/$total: ${q.question}');
    for (var j = 0; j < q.options.length; j++) {
      print('  ${j + 1}. ${q.options[j]}');
    }

    stdout.write('Your answer (1-${q.options.length}): ');
    final input = stdin.readLineSync();
    final userAnswer = int.tryParse(input ?? '') ?? -1;

    if (userAnswer - 1 == q.answerIndex) {
      print('✅ Correct!');
      score++;
    } else {
      print('❌ Wrong! Correct answer: ${q.options[q.answerIndex]}');
    }
  }

  print(
    '\n🎉 Quiz finished! Your score: $score / $total (${(score / total * 100).toStringAsFixed(0)}%) ',
  );
}

Future<void> main(List<String> args) async {
  final path = args.isNotEmpty ? args[0] : 'questions.json';
  if (!await File(path).exists()) {
    stderr.writeln('Không tìm thấy file câu hỏi: $path');
    exit(1);
  }
  final questions = await loadQuestions(path);
  print('+++++Chương trình Quiz Dart+++++');
  startQuiz(questions);
}
