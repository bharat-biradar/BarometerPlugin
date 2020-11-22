import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:barometer_plugin/barometer_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _reading = 'Not yet taken';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await BarometerPlugin.platformVersion;
      await BarometerPlugin.initialiseBarometer();
      final _reading = await BarometerPlugin.reading;
      print('reading is $_reading');
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            Text('reading is $_reading'),
            RaisedButton(
              onPressed: () async {
                final reading = await BarometerPlugin.reading;
                setState(() {
                  _reading = reading.toString();
                });
              },
              child: Text('get latest reading'),
            ),
            StreamBuilder(
              stream: BarometerPlugin.pressureStream,
              builder: (context, AsyncSnapshot<double> snapshot ) {
                return Text(snapshot.data.toString());
              },
            )
          ],
        ),
      ),
    );
  }
}
