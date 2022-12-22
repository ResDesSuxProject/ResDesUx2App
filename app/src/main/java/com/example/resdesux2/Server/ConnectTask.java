package com.example.resdesux2.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class ConnectTask extends AsyncTask<Void, Void, Socket> {
    private static final String TAG = "Connect Task";
    private final String serverAddress;
    private final int serverPort;
    private final ServerService serverService;

    public ConnectTask(String serverAddress, int serverPort, ServerService serverService) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverService = serverService;
    }

    protected void onPostExecute(Socket socket) {
        try {
            serverService.connectToServer(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected Socket doInBackground(Void... voids) {
        while(true) {
            try {
                // Create a socket and connect to the server
                return new Socket(serverAddress, serverPort);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Can't connect to server, reconnecting...");
                Log.e(TAG, e.toString());
            }
        }
    }
}
