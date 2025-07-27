import 'package:flutter/services.dart';

class RfidReaderPlugin {
  static const MethodChannel _channel = MethodChannel('rfid_reader_plugin');

  static Future<bool> init() async {
    final result = await _channel.invokeMethod<bool>('init');
    return result ?? false;
  }

  static Future<void> startReading() async {
    await _channel.invokeMethod('startReading');
  }

  static Future<void> stopReading() async {
    await _channel.invokeMethod('stopReading');
  }

  static Future<List<String>> getTags() async {
    final List<dynamic> tags = await _channel.invokeMethod('getTags');
    return tags.cast<String>();
  }
}
