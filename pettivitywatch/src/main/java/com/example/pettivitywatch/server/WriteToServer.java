package com.example.pettivitywatch.server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;

public class WriteToServer extends Thread {
    private static final String TAG = "WriteToServer";
    private final BufferedWriter writer;
    private final Handler handler;
    private final String message;

    public WriteToServer(String message, BufferedWriter writer, Handler handler) {
        this.message = message;
        this.writer = writer;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        try {
            writer.write(String.format("%s\n", message));
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "run: socket closed\n" + e);
            // Create a new Message object
            Message message = handler.obtainMessage();

            // Say what the message is about
            message.what = ServerHandler.MESSAGE_DISCONNECTED;

            // Send message
            handler.sendMessage(message);
        }
    }
}
