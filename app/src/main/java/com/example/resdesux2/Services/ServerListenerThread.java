package com.example.resdesux2.Services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerListenerThread extends Thread {
    private static final String TAG = "ServerListenerThread";
    public Handler handler;
    private final ServerService serverService;
    private final BufferedReader reader;


    public ServerListenerThread(String name, ServerService serverService, BufferedReader reader) {
        super(name);
        this.serverService = serverService;
        this.reader = reader;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                String line = reader.readLine();

                serverService.processIncoming(line);

            } catch (IOException e) {
                Log.e(TAG, "run: socket closed\n" + e);
                serverService.reconnect();
                return;
            }
        }

//        handler = new Handler(getLooper(), this::handleMessages);
    }





    public boolean handleMessages(Message message) {
        Log.e(TAG, "handleMessages: " + message);
        return true;
    }
}
