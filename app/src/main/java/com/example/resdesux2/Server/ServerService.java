package com.example.resdesux2.Server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.resdesux2.Models.ChangeListener;
import com.example.resdesux2.Models.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerService extends Service {
    static final String IP_ADDRESS = "192.168.31.62"; //  "10.0.2.2"; //192.168.31.62";
    static final int SERVER_PORT = 9999;
    private static final String TAG = "Server Service";
    public static final int MESSAGE_FROM_SERVER = 100;
    public static final int MESSAGE_DISCONNECTED = 99;
    public boolean isConnected = false;
    private final IBinder binder = new ServiceBinder();
    private boolean isRunning = false;

    /* ----- Server ----- */
    private Socket socket;
    private  BufferedWriter writer;
    ServerListenerThread listenerThread;
    public Handler mainThreadHandler;


    /* ----- listeners ----- */
    // change listeners
    private ChangeListener<Boolean> connectedListener;
    private ChangeListener<Double> scoreListener;
    // single listeners
    private ArrayList<ChangeListener<ArrayList<User>>> userListeners = new ArrayList<>();

    private double score = -1;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only create a new Thread when it is just starting up
        if (!isRunning) {
            new ConnectTask(IP_ADDRESS, SERVER_PORT, this).execute();
            // Create a new Handler on the UI thread so we can communicate with the other thread
            mainThreadHandler = new Handler(Looper.getMainLooper(), this::handleMessage);

            isRunning = true;
        }
        return START_STICKY;
    }

    public void connectToServer(Socket socket) throws IOException {
        // Store the socket so it can latter be closed
        this.socket = socket;

        // create a reader and writer to communicate with the server
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Setup the thread to listen to the server
        listenerThread = new ServerListenerThread("Server listener", mainThreadHandler, reader, writer);
        listenerThread.start();
        isConnected = true;

        // Send a message to the server which includes name and a listener for the score
        String request = "name: Mobile client " + socket.getLocalSocketAddress().toString().replace("/", "").replace(":", ";") + "\n";
        request += "listen_score: 0\n";
        new WriteToServer(request, writer, mainThreadHandler).start();

        Log.i(TAG, "connectToServer: Connected to server " + socket.getRemoteSocketAddress());

        // Once connected let the listener know that a connection has been established
        if (connectedListener != null) {
            connectedListener.onChange(true);
        }
    }

    private boolean handleMessage(Message message) {
        Log.i(TAG, "handleMessage: " + message.getData());

        switch (message.what) {
            // If the message is about data received from the server
            case MESSAGE_FROM_SERVER:
                try {
                    processIncoming(message.getData().getString("line"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            // If the server has been disconnected
            case MESSAGE_DISCONNECTED:
                reconnect();
            break;
        }
        return true;
    }

    public void processIncoming(String line) throws IOException {
        Log.i(TAG, "processIncoming: " + line);

        // First find the command and prepare it
        String[] arguments = line.split(":");
        if (arguments.length == 0) return;
        String command = arguments[0].toLowerCase().trim();

        // Then prepare the sub command
        if (arguments.length >= 2)
            arguments[1] = arguments[1].trim();

        switch (command) {
            case "score":
                score = Double.parseDouble(arguments[1]);
                if (scoreListener != null)
                    scoreListener.onChange(score);
//                for (ChangeListener<Double> changeListener : scoreListeners) {
//                    changeListener.onChange(score);
//                }
                break;
            case "users":
                String[] incomingUsers = arguments[1].split("\\|");
                users = new ArrayList<>();
                for (String incomingUser : incomingUsers) {
                    String[] userData = incomingUser.split(",");
                    if (userData.length != 4) continue;
                    users.add(new User(Integer.parseInt(userData[0]), userData[1], Double.parseDouble(userData[2]), Integer.parseInt(userData[3])));
                }

                // update all the listeners and then delete them
                for (ChangeListener<ArrayList<User>> listener : userListeners) {
                    listener.onChange(users);
                }
                userListeners = new ArrayList<>();
                break;
        }
    }

    /* ---------- Listeners ---------- */
    public void setConnectionListener(ChangeListener<Boolean> listener) {
        if (isConnected) listener.onChange(true);
        else connectedListener = listener;
    }

    public void setScoreListener(ChangeListener<Double> listener) {
        scoreListener = listener;
        if (score != -1)
            scoreListener.onChange(score);
    }

    public void getUsers(ChangeListener<ArrayList<User>> listener) {
        userListeners.add(listener);
        Thread thread = new WriteToServer("get_users: 0", writer, mainThreadHandler);
        thread.start();
    }

    public void reconnect(){
        listenerThread.interrupt();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }

        isConnected = false;
        new ConnectTask(IP_ADDRESS, SERVER_PORT, this).execute();
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
