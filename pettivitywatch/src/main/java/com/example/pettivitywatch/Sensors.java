package com.example.pettivitywatch;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

import com.example.pettivitywatch.models.HeartRateQueue;
import com.example.pettivitywatch.models.ChangeListener;

public class Sensors {
    private static final String TAG = "Sensors";
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private HeartRateQueue heartRateQueue;
    private ChangeListener<Float> changeListener;

    /**
     * Callback about changes in the sensors
     */
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            changeListener.onChange(sensorEvent.values[0]);
            // If a heartRateQueue has been registered add it to it.
            if (heartRateQueue != null && sensorEvent.values[0] > 0) {
                heartRateQueue.addToQueue((int) sensorEvent.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {  }
    };

    public Sensors(@NonNull Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void register(HeartRateQueue heartRateQueue, ChangeListener<Float> changeListener) {
        this.heartRateQueue = heartRateQueue;
        this.changeListener = changeListener;
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener, sensor);
    }
}
