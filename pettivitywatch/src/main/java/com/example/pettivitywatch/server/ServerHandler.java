package com.example.pettivitywatch.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pettivitywatch.models.Change2Listener;
import com.example.pettivitywatch.models.ChangeListener;
import com.example.pettivitywatch.models.ServerInteraction;
import com.example.pettivitywatch.models.User;
import com.example.pettivitywatch.server.ConnectTask;
import com.example.pettivitywatch.server.ServerListenerThread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class ServerHandler implements ServerInteraction {
    private static final String TAG = "Server Service";
    public static final int MESSAGE_FROM_SERVER = 100;
    public static final int MESSAGE_DISCONNECTED = 99;
    public static final int MESSAGE_FAILED_CONNECTION = 98;
    public static final int MESSAGE_SENSOR_CONNECTED = 101;

    // Server connections
    public Handler mainThreadHandler;
    private ConnectTask connectTask;
    private Socket socket;
    private BufferedWriter writer;
    private ServerListenerThread listenerThread;


    // Server variables
    public boolean isConnected = false;
    private SharedPreferences sharedPreferences;
    private String serverIp;
    private static final int serverPort = 9999;

    // user
    private int currentUserID = -1;
    private User currentUser = null;

    /* ----- listeners ----- */
    // change listeners
    private ChangeListener<Boolean> connectedListener;
    private ChangeListener<Boolean> connectionFailedListener;
    private final ArrayList<Change2Listener<Integer, Integer>> scoreListeners = new ArrayList<>();
    private ChangeListener<Integer> loginListener;
    // single listeners
    private ArrayList<ChangeListener<ArrayList<User>>> userListeners = new ArrayList<>();
    private ArrayList<ChangeListener<User>> currentUserListener = new ArrayList<>();

    private User.Score score = null;
    private ArrayList<User> users = new ArrayList<>();


    public ServerHandler(AppCompatActivity activity) {
        // Get or create SharedPreferences
        sharedPreferences = activity.getSharedPreferences("PettivityWatch", Context.MODE_PRIVATE);
        serverIp = getServerIP();

        currentUserID = getLoggedInUser();

        // Create a new Handler on the UI thread so we can communicate with the other thread
        mainThreadHandler = new Handler(Looper.getMainLooper(), this::handleMessage);

        // Run a task in the background to connect to the server
        connectTask = new ConnectTask(serverIp, serverPort, this::connectToServer, mainThreadHandler);
        connectTask.execute();
    }

    /**
     * Connect to server is called form ConnectTask when the server is (re)connected
     * @param socket The socket that connects to the server
     */
    public void connectToServer(Socket socket) {
        // Store the socket so it can latter be closed
        this.socket = socket;

        // create a reader and writer to communicate with the server
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException exception){
            throw new RuntimeException("Couldn't create reader or writer from server");
        }

        // Setup the thread to listen to the server
        listenerThread = new ServerListenerThread("Server listener", mainThreadHandler, reader, writer);
        listenerThread.start();
        isConnected = true;

        // Send a message to the server which includes name and a listener for the score
        String request = "name: Mobile client " + socket.getLocalSocketAddress().toString().replace("/", "").replace(":", ";") + "\n";
        if (currentUserID != -1) {
            request += "listen_score: " + currentUserID;
            request += "\nget_user: " + currentUserID;
        }
        new WriteToServer(request, writer, mainThreadHandler).start();

        Log.i(TAG, "connectToServer: Connected to server " + socket.getRemoteSocketAddress());

        // Once connected let the listener know that a connection has been established
        if (connectedListener != null) {
            connectedListener.onChange(true);
        }
    }

    /**
     * This function gets triggered once a thread talks to the mainThreadHandler,
     * And processes the message and send it through to the right method.
     * @param message The message from the handler
     * @return always true
     */
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

            // If it failed to connect to the server
            case MESSAGE_FAILED_CONNECTION:
                if (connectionFailedListener != null) {
                    connectionFailedListener.onChange(false);
                }
                break;
        }
        return true;
    }

    /**
     * Processes the incoming data from the server.
     * Is called from a message send from ServerListenerThread
     * @param line the incoming command and arguments from the server
     * @throws IOException If an I/O error occurs when creating the input stream,
     *                      the socket is closed, the socket is not connected,
     *                      or the socket input has been shutdown using shutdownInput()
     */
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
                String[] scores = arguments[1].split(",");

                int _score = scores.length >= 2 ? Integer.parseInt(scores[0]) : (int) Double.parseDouble(scores[0]);

                score = new User.Score(_score, scores.length >= 2 ? Integer.parseInt(scores[1]) : _score);
                Log.i(TAG, "processIncoming: " + scoreListeners);
                if (scoreListeners.size() > 0) {
                    for (Change2Listener<Integer, Integer> listener : scoreListeners){
                        listener.onChange(score.getIntensityScore(), score.getFrequencyScore());
                    }
                }

                currentUser.setScore(score);
                break;
            case "user":
                String[] currentUserData = arguments[1].split(",");

                // create a new user from the data. length 4 is the legacy 1 score approach and length 5 is the new two score approach
                User _user = createUserFromData(currentUserData);
                if (_user == null) break;
                currentUser = _user;

                // Update all the listeners and then delete them
                for (ChangeListener<User> listener : currentUserListener) {
                    listener.onChange(currentUser);
                }
                currentUserListener = new ArrayList<>();
                break;
            case "users":
                String[] incomingUsers = arguments[1].split("\\|");
                users = new ArrayList<>();
                for (String incomingUser : incomingUsers) {
                    String[] userData = incomingUser.split(",");

                    User user = createUserFromData(userData);
                    if (user == null) continue;
                    users.add(user);
                }

                // Update all the listeners and then delete them
                for (ChangeListener<ArrayList<User>> listener : userListeners) {
                    listener.onChange(users);
                }
                userListeners = new ArrayList<>();
                break;
            case "login":
                int loginId = Integer.parseInt(arguments[1]);
                setLoggedInUser(loginId);
                if (loginListener != null)
                    loginListener.onChange(loginId);
                break;
        }
    }

    /**
     * Reconnects to the server and stops the listenerThread
     */
    private void reconnect(){
        if (connectedListener != null) {
            connectedListener.onChange(false);
        }
        isConnected = false;

        // Disconnect everything first
        if (listenerThread != null) listenerThread.interrupt();
        if (connectTask != null) connectTask.cancel(true);

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }

        // Run a task in the background to connect to the server
        connectTask = new ConnectTask(serverIp, serverPort, this::connectToServer, mainThreadHandler);
        connectTask.execute();

        Log.i(TAG, "reconnecting..");
    }

    @Override
    public void login(String username, String password, ChangeListener<Integer> listener) {

    }

    @Override
    public void setScoreListener(Change2Listener<Integer, Integer> listener) {

    }


    /**
     * Retrieves the server IP from the shared preferences (local storage)
     * @return The stored server IP
     */
    public String getServerIP() {
        return sharedPreferences.getString("SERVER_IP", "");
    }

    /**
     * Updates the server IP in the local storage
     * And disconnects to the current server and connects to the new one
     * @param serverIP the new server IP in the form of 0.0.0.0
     */
    public void updateServerIP(String serverIP) {
        sharedPreferences.edit().putString("SERVER_IP", serverIP).apply();
        this.serverIp = serverIP;
        reconnect();
    }

    public int getLoggedInUser() {
        return sharedPreferences.getInt("USER", -1);
    }
    private void setLoggedInUser(int userID) {
        currentUserID = userID;
        sharedPreferences.edit().putInt("USER", userID).apply();

        // request the score listener for the new user and info about the new user
        String request = String.format(Locale.US, "listen_score: %d\nget_user: %d", this.currentUserID, this.currentUserID);
        new WriteToServer(request, writer, mainThreadHandler).start();
    }

    private User createUserFromData(String[] userData) {
        // The old legacy way with one score
        if (userData.length == 4) {
            int score = (int) Double.parseDouble(userData[2]);
            return new User(Integer.parseInt(userData[0]),
                    userData[1],
                    score,
                    score,
                    Integer.parseInt(userData[3]));
        } // The new approach with two different score dimensions
        else if (userData.length == 5) {
            return new User(Integer.parseInt(userData[0]),
                    userData[1],
                    Integer.parseInt(userData[2]),
                    Integer.parseInt(userData[3]),
                    Integer.parseInt(userData[4]));
        }
        return null;
    }
}
