package com.example.resdesux2.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.resdesux2.Models.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerService extends Service {
    static final String IP_ADDRESS = "192.168.31.62"; //  "10.0.2.2"; //192.168.31.62";
    static final int SERVER_PORT = 9999;
    private static final String TAG = "Server Service";
    public boolean isConnected = false;
    private final IBinder binder = new ServiceBinder();
    private boolean isRunning = false;

    /* ----- Server ----- */
    private Socket socket;
    private  BufferedWriter writer;
    ServerListenerThread listenerThread;

    /* ----- listeners ----- */
    private ArrayList<ChangeListener<Double>> scoreListeners = new ArrayList<>();

    private double score;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only create a new Thread when it is just starting up
        if (!isRunning) {
            new ConnectTask(IP_ADDRESS, 9999).execute(this);

            isRunning = true;
        }

        return START_STICKY;
    }

    public void connectToServer(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Send a message to the server
        writer.write("name: Mobile client " + socket.getLocalSocketAddress().toString().replace("/", "") + "\n");
        writer.write("listen_score: 0\n");
        writer.flush();


        Log.i(TAG, "connectToServer: Connected to server " + socket.getRemoteSocketAddress());

        listenerThread = new ServerListenerThread("Test", this, reader);
        listenerThread.start();
        isConnected = true;

        writer.write("get_users\n");
        writer.flush();
    }
    
    public void processIncoming(String line) throws IOException {
        if (line.contains("ping")) {
            writer.write("pong\n");
            writer.flush();
            return;
        }
        
        Log.i(TAG, "processIncoming: " + line);

        String[] arguments = line.split(":");
        if (arguments.length == 0) return;
        String command = arguments[0].toLowerCase().trim();

        if (arguments.length >= 2)
            arguments[1] = arguments[1].trim();

        switch (command) {
            case "score":
                score = Double.parseDouble(arguments[1]);
                for (ChangeListener<Double> changeListener : scoreListeners) {
                    changeListener.onChange(score);
                }
                break;
            case "users":
                String[] incomingUsers = arguments[1].split("\\|");
                for (String incomingUser : incomingUsers) {
                    String[] userData = incomingUser.split(",");
                    if (userData.length != 3) continue;
                    users.add(new User(Integer.parseInt(userData[0]), userData[1], Double.parseDouble(userData[2])));
                }
                Log.i(TAG, "processIncoming: " + Arrays.toString(users.toArray()));
                break;
        }
    }

    public void reconnect(){
        listenerThread.interrupt();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
        isConnected = false;
        new ConnectTask(IP_ADDRESS, 9999).execute(this);
        isRunning = true;

        Log.i(TAG, "reconnecting..");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null && socket.isConnected()) {
            // Close the socket when done
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    public class ServiceBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
}
