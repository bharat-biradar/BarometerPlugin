package com.b.biradar.barometer_plugin;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;

import static android.content.Context.SENSOR_SERVICE;

/**
 * BarometerPlugin
 */
public class BarometerPlugin implements FlutterPlugin, MethodCallHandler, SensorEventListener, EventChannel.StreamHandler {

    private MethodChannel channel;
    private SensorManager mSensorManager;
    private Sensor mBarometer;
    private Context context;
    private static final String channelName = "barometer_plugin";
    private float mLatestReading = 0;
    private EventChannel.EventSink meventSink;


    private static void setup(BarometerPlugin plugin, BinaryMessenger binaryMessenger, Context context) {
        plugin.channel = new MethodChannel(binaryMessenger, channelName);
        plugin.channel.setMethodCallHandler(plugin);
        plugin.context = context;
        final EventChannel eventChannel = new EventChannel(binaryMessenger, "pressureStream");
        eventChannel.setStreamHandler(plugin);
    }

    public static void registerWith(Registrar registrar) {
        BarometerPlugin barometerPlugin = new BarometerPlugin();
        setup(barometerPlugin, registrar.messenger(), registrar.context());
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        setup(this, flutterPluginBinding.getBinaryMessenger(), flutterPluginBinding.getApplicationContext());
    }

    boolean initialiseBarometer() {
        mSensorManager = (SensorManager) (context.getSystemService(SENSOR_SERVICE));
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mLatestReading = event.values[0];
        if(meventSink !=null){
            meventSink.success(mLatestReading);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    double getBarometer() {
        return mLatestReading;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
            return;
        }

        if (call.method.equals("getBarometer")) {
            double reading = getBarometer();
            result.success(reading);
            return;
        }

        if (call.method.equals("initializeBarometer")) {
            result.success(initialiseBarometer());
            return;
        }
        result.notImplemented();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }


    @Override
    public void onListen(Object arguments, EventChannel.EventSink eventSink) {
        meventSink =eventSink;

    }

    @Override
    public void onCancel(Object arguments) {
        meventSink=null;
    }
}
