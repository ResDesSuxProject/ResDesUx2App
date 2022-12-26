package com.example.resdesux2.Server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ServerListenerThread extends Thread {
    private static final String TAG = "ServerListenerThread";
    public Handler handler;
    private final BufferedReader reader;
    private final BufferedWriter writer;


    public ServerListenerThread(String name, Handler handler, BufferedReader reader, BufferedWriter writer) {
        super(name);
        this.reader = reader;
        this.handler = handler;
        this.writer = writer;
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();

        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) continue;
                Log.v(TAG, "received: " + line);

                // If the server pinged us respond with a pong
                if (line.contains("ping")) {
                    writer.write("pong\n");
                    writer.flush();
                    continue;
                }

                // ----- Send message to the main thread -----
                // Create a new Message object
                Message message = handler.obtainMessage();

                // Format and attach data
                Bundle data = new Bundle();
                data.putString("line", line);
                message.setData(data);
                message.what = ServerService.MESSAGE_FROM_SERVER;

                // Send message
                handler.sendMessage(message);
            } catch (IOException e) {
                Log.e(TAG, "run: socket closed\n" + e);
                // Create a new Message object
                Message message = handler.obtainMessage();

                // Say what the message is about
                message.what = ServerService.MESSAGE_DISCONNECTED;

                // Send message
                handler.sendMessage(message);
                return;
            }
        }
    }
}
