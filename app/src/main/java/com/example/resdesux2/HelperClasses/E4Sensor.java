//package com.example.resdesux2.HelperClasses;
//
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import com.empatica.empalink.ConnectionNotAllowedException;
//import com.empatica.empalink.EmpaDeviceManager;
//import com.empatica.empalink.EmpaticaDevice;
//import com.empatica.empalink.config.EmpaSensorType;
//import com.empatica.empalink.config.EmpaStatus;
//import com.empatica.empalink.delegate.EmpaDataDelegate;
//import com.empatica.empalink.delegate.EmpaStatusDelegate;
//import com.example.resdesux2.Server.ServerService;
//
//public class E4Sensor implements EmpaDataDelegate, EmpaStatusDelegate {
//    private static final String TAG = "E4Sensor";
//    private final EmpaDeviceManager deviceManager;
//    private final Handler handler;
//
//    public E4Sensor(Context context, Handler handler) {
//        Looper.prepare();
//        deviceManager = new EmpaDeviceManager(context.getApplicationContext(), this, this);
//        deviceManager.authenticateWithAPIKey("YOUR API KEY");
//
//        this.handler = handler;
//    }
//
//    @Override
//    public void didDiscoverDevice(EmpaticaDevice device, String deviceLabel, int rssi, boolean allowed) {
//        Log.i(TAG, "didDiscoverDevice: Device name: " + device.name + ", " + device.info + " Can connect: " + allowed);
//        if (allowed) {
//            try {
//                deviceManager.connectDevice(device);
//
//                // Send service a new message
//                Message message = handler.obtainMessage();
//                message.what = ServerService.MESSAGE_SENSOR_CONNECTED;
//                handler.sendMessage(message);
//
//            } catch (ConnectionNotAllowedException e) {
//                Log.e(TAG, "didDiscoverDevice: Not allowed to connect to: " + device.info);
//            }
//        }
//    }
//
//    /**
//     * PPG Sensor
//     * Measures Blood Volume Pulse (BVP), from which heart rate variability can be derived
//     */
//    @Override
//    public void didReceiveBVP(float bvp, double timestamp) {
//
//    }
//
//    /**
//     * EDA Sensor (GSR Sensor)
//     * Measures the constantly fluctuating changes in certain electrical properties of the skin
//     */
//    @Override
//    public void didReceiveGSR(float gsr, double timestamp) {
//
//    }
//
//
//    @Override
//    public void didReceiveIBI(float ibi, double timestamp) {
//
//    }
//
//    @Override
//    public void didReceiveTemperature(float t, double timestamp) {
//
//    }
//
//    @Override
//    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
//
//    }
//
//    @Override
//    public void didReceiveBatteryLevel(float level, double timestamp) {
//
//    }
//
//    @Override
//    public void didReceiveTag(double timestamp) {
//
//    }
//
//    @Override
//    public void didUpdateStatus(EmpaStatus status) {
//
//    }
//
//    @Override
//    public void didEstablishConnection() {
//
//    }
//
//    @Override
//    public void didUpdateSensorStatus(int status, EmpaSensorType type) {
//
//    }
//
//
//    @Override
//    public void didFailedScanning(int errorCode) {
//
//    }
//
//    @Override
//    public void didRequestEnableBluetooth() {
//
//    }
//
//    @Override
//    public void bluetoothStateChanged() {
//
//    }
//
//    @Override
//    public void didUpdateOnWristStatus(int status) {
//
//    }
//}
