package com.example.resdesux2.Server;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Socket> {
    private static final String TAG = "Connect Task";
    private final String serverAddress;
    private final int serverPort;
    private final ServerService serverService;
    private final Handler handler;

    public ConnectTask(String serverAddress, int serverPort, ServerService serverService, Handler handler) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverService = serverService;
        this.handler = handler;
    }

    protected void onPostExecute(Socket socket) {
        if (socket == null) return;

        try {
            serverService.connectToServer(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Socket doInBackground(Void... voids) {
        while(!isCancelled()) {
            try {
                // Create a socket and connect to the server
//                return new Socket(serverAddress, serverPort);
                Socket socket = new Socket();
//                socket.setSoTimeout(2000);
                socket.connect(new InetSocketAddress(serverAddress, serverPort), 2000);
                if (socket.isConnected()){
                    return socket;
                }
            } catch (IOException e) {
//                Log.i(TAG, "doInBackground: Can't connect to server, reconnecting...");
//                Log.e(TAG, e.toString());
            }
            // Send service a new message
            Message message = handler.obtainMessage();
            message.what = ServerService.MESSAGE_FAILED_CONNECTION;
            handler.sendMessage(message);
            // TODO make this thread wait a bit
        }
        return null;
    }
}
