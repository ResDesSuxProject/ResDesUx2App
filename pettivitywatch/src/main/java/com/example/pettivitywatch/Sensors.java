package com.example.pettivitywatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class Sensors {
    private static final String TAG = "Sensors";
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private TextView textView;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
//            Log.v(TAG, "onSensorChanged: " + sensorEvent.values[0]);
            if (textView != null && sensorEvent.values[0] > 0) {
                textView.setText(String.format("Heart rate: %.0f", sensorEvent.values[0]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {  }
    };

    public Sensors(@NonNull Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void register(TextView textView) {
        this.textView = textView;
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener, sensor);
    }
}
