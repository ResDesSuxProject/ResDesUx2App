package com.example.resdesux2.Services;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.xml.transform.Result;

public class ConnectTask extends AsyncTask<ServerService, Void, Void> {
    private String mServerAddress;
    private int mServerPort;

    public ConnectTask(String serverAddress, int serverPort) {
        mServerAddress = serverAddress;
        mServerPort = serverPort;
    }

    protected void onPostExecute(Result result) {

    }

    @Override
    protected Void doInBackground(ServerService... serverServices) {
        try {
            // Create a socket and connect to the server
            Socket socket = new Socket(mServerAddress, mServerPort);
            serverServices[0].connectToServer(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
