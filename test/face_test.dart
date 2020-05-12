import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:face/face_detect_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('face');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
  });
}
