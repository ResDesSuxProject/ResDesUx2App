package com.example.pettivitywatch.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.pettivitywatch.models.ChangeListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Socket> {
    private static final String TAG = "Connect Task";
    private final String serverAddress;
    private final int serverPort;
    private final Handler handler;
    private final ChangeListener<Socket> connectToServerListener;
    private int sleepTime = 2000;

    public ConnectTask(String serverAddress, int serverPort, ChangeListener<Socket> connectToServerListener, Handler handler) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        this.handler = handler;
        this.connectToServerListener = connectToServerListener;
        Log.e(TAG, "ConnectTask: " + serverAddress + ":" + serverPort);
    }

    protected void onPostExecute(Socket socket) {
        if (socket == null) return;

        connectToServerListener.onChange(socket);
    }

    @Override
    protected Socket doInBackground(Void... voids) {
        int attempts = 0;
        while(!isCancelled()) {
            try {
                // Create a socket and connect to the server
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 2000);
                if (socket.isConnected()){
                    return socket;
                }
            } catch (IOException ignored) {}

            // Send service a new message
            Message message = handler.obtainMessage();
            message.what = ServerHandler.MESSAGE_FAILED_CONNECTION;
            handler.sendMessage(message);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                break;
            }
            attempts++;
            if (attempts % 5 == 0 && attempts <= 40) {
                sleepTime *= 2;
            }
        }
        return null;
    }
}
