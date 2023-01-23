package com.example.pettivitywatch;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;

public class Sensors {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView textView;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.d("Sensors", "onSensorChanged: " + sensorEvent.values[0]);
            if (textView != null) {
                textView.setText((int) sensorEvent.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    public Sensors(Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void register(TextView textView) {
        this.textView = textView;
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        Log.e("Sensors", "register: registered");
    }

    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener, sensor);
    }
}
