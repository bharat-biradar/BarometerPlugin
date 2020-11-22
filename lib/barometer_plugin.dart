import 'dart:async';

import 'package:flutter/services.dart';

class BarometerPlugin {
  static const MethodChannel _channel = const MethodChannel('barometer_plugin');

  static const EventChannel _eventChannel =
      const EventChannel('pressureStream');

  static Stream _pressureStream;

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<double> get reading async {
    final double reading = await _channel.invokeMethod('getBarometer');
    return reading;
  }

  static Future<bool> initialiseBarometer() async {
    final bool initialized = await _channel.invokeMethod('initializeBarometer');
    return initialized;
  }

  static Stream<double> get pressureStream {
    if (_pressureStream == null) {
      _pressureStream = _eventChannel.receiveBroadcastStream().map<double>((event) => event);
    }
    return _pressureStream;
  }
}
