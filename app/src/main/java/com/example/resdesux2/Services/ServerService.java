package com.example.resdesux2.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerService extends Service {
    static final String ipAddress = "192.168.31.62";
    static final int server_port = 9999;
    private final IBinder binder = new ServiceBinder();
    private Handler handler = null;
    private static Runnable runnable = null;
    private boolean isRunning = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only create a new Thread when it is just starting up
        if (!isRunning) {

            // Connect to the server
            Socket socket = null;
//            while (socket == null || !socket.isConnected()) {
                try {
                    socket = new Socket(ipAddress, server_port);
                } catch (IOException e) {
                    Toast.makeText(this, "Couldn't connect, retrying..", Toast.LENGTH_SHORT).show();
                }
//            }
            if (socket != null)
                Toast.makeText(this, "Connected to server " + socket.getRemoteSocketAddress(), Toast.LENGTH_SHORT).show();

            // Create a new thread to handle the connection
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();


//        if (!isRunning) {
//            Toast.makeText(context, "Service created!", Toast.LENGTH_SHORT).show();
//            handler = new Handler();
//            runnable = new Runnable() {
//                public void run() {
//                    Toast.makeText(context, "Service is still running", Toast.LENGTH_SHORT).show();
//                    handler.postDelayed(runnable, 10000);
//                }
//            };
//
//            handler.postDelayed(runnable, 15000);
            isRunning = true;
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    public class ServiceBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
}
