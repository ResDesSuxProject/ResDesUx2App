package com.example.resdesux2.Server;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.resdesux2.Models.Change2Listener;
import com.example.resdesux2.Models.ChangeListener;
import com.example.resdesux2.Models.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class ServerService extends ForegroundService {
    private static final String TAG = "Server Service";
    public static final int MESSAGE_FROM_SERVER = 100;
    public static final int MESSAGE_DISCONNECTED = 99;
    public static final int MESSAGE_FAILED_CONNECTION = 98;

    private final IBinder binder = new ServiceBinder();
    private boolean isRunning = false;

    /* ----- Server ----- */
    static final int SERVER_PORT = 9999;
    private String server_IP = "";

    public boolean isConnected = false;
    public Handler mainThreadHandler;
    private Socket socket;
    private  BufferedWriter writer;
    private ConnectTask connectTask;
    ServerListenerThread listenerThread;
    SharedPreferences sharedPreferencesServer;

    private int currentUserID = -1;
    private User currentUser = null;

    /* ----- listeners ----- */
    // change listeners
    private ChangeListener<Boolean> connectedListener;
    private ChangeListener<Boolean> connectionFailedListener;
    private Change2Listener<Integer, Integer> scoreListener;
    private ChangeListener<Integer> loginListener;

    // single listeners
    private ArrayList<ChangeListener<ArrayList<User>>> userListeners = new ArrayList<>();
    private ArrayList<ChangeListener<User>> currentUserListener = new ArrayList<>();

    private User.Score score = null;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Only create a new Thread when it is just starting up
        if (!isRunning) {
            super.onStartCommand(intent, flags, startId);
            // Get or create SharedPreferences
            sharedPreferencesServer = getSharedPreferences("SERVER_PREFERENCE", MODE_PRIVATE);
            server_IP = getServerIP();
            // Set the current user to the last logged in, this is for the background and widgets
            currentUserID = getLoggedInUser();
            currentUser = new User(-1, "", 0, 0, 20);

            // Create a new Handler on the UI thread so we can communicate with the other thread
            mainThreadHandler = new Handler(Looper.getMainLooper(), this::handleMessage);

            // Run a task in the background to connect to the server
            connectTask = new ConnectTask(server_IP, SERVER_PORT, this::connectToServer, mainThreadHandler);
            connectTask.execute();

            isRunning = true;

        }
        return START_STICKY;
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

        // Update the notification to reflect the current state
        updateNotification(true);
    }

    /**
     * This function gets triggered once a thread talks to the mainThreadHandler,
     * And processes the message and send it through to the right method.
     * @param message The message from the handler
     * @return always true
     */
    private boolean handleMessage(Message message) {
//        Log.i(TAG, "handleMessage: " + message.getData());

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

                if (scoreListener != null)
                    scoreListener.onChange(score.getIntensityScore(), score.getFrequencyScore());

                currentUser.setScore(score);
                updateWidget(currentUser);
                break;
            case "user":
                String[] currentUserData = arguments[1].split(",");

                // create a new user from the data. length 4 is the legacy 1 score approach and length 5 is the new two score approach
                User _user = createUserFromData(currentUserData);
                if (_user == null) break;
                currentUser = _user;
                updateWidget(currentUser);

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
                setCurrentUserID(loginId);
                if (loginListener != null)
                    loginListener.onChange(loginId);
                break;
        }
    }

    /* ------------------------------ Listeners ------------------------------ */
    /**
     * Set a listener that gets triggered once the server has been connected to this service
     * @param listener a listener which has an onChange method taking a boolean.
     *                  Or a method that takes an boolean and then write this::METHOD_NAME
     */
    public void setConnectionListener(ChangeListener<Boolean> listener) {
        connectedListener = listener;
        if (isConnected) listener.onChange(true);
    }

    /**
     * Sets the connection failed listener which gets triggered everytime the connection
     * to the server can't be established.
     * Used in BoundActivity
     * @param listener the listener which gets called with a Boolean
     */
    public void setConnectionFailedListener(ChangeListener<Boolean> listener) {
        connectionFailedListener = listener;
    }

    /**
     * Set a listener that gets triggered constantly when the score of the logged in user changes
     * The listener method called needs and int intensity and an int frequency scores.
     * @param listener a listener which has an onChange method taking a Double.
     *                  Or a method that takes two doubles and then write this::METHOD_NAME
     */
    public void setScoreListener(Change2Listener<Integer, Integer> listener) {
        scoreListener = listener;
        if (score != null)
            scoreListener.onChange(score.getIntensityScore(), score.getFrequencyScore());
    }

    /**
     * Request users from the server and Set a listener that gets triggered once the users are received
     * @param listener a listener which has an onChange method taking a ArrayList of Users.
     *                  Or a method that takes an <ArrayList<User>> and then write this::METHOD_NAME
     */
    public void getUsers(ChangeListener<ArrayList<User>> listener) {
        userListeners.add(listener);
        Thread thread = new WriteToServer("get_users: 0", writer, mainThreadHandler);
        thread.start();
    }

    public void getCurrentUser(ChangeListener<User> listener) {
        if (currentUser != null) {
            listener.onChange(currentUser);
        } else {
            currentUserListener.add(listener);
        }
    }

    /**
     * Queries the server if the login is valid, if it is the id of the user is returned.
     * If not then -1 is returned
     * @param username The username of the given user
     * @param password The password the user entered
     * @param listener The lister that gets called when the server responds.
     */
    public void login(String username, String password, ChangeListener<Integer> listener){
        loginListener = listener;
        Thread thread = new WriteToServer(String.format("login: %s", username.trim()), writer, mainThreadHandler);
        thread.start();
    }

    /**
     * Reconnects to the server and stops the listenerThread
     */
    private void reconnect(){
        // Reflect the state in the notification
        isConnected = false;
        updateNotification(false);

        // Disconnect everything first
        if (listenerThread != null) listenerThread.interrupt();
        if (connectTask != null) connectTask.cancel(true);

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }

        // Run a task in the background to connect to the server
        connectTask = new ConnectTask(server_IP, SERVER_PORT, this::connectToServer, mainThreadHandler);
        connectTask.execute();

        isRunning = true;

        Log.i(TAG, "reconnecting..");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        if (socket != null && socket.isConnected()) {
            // Close the socket when done
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public int getCurrentUserID() {
        return currentUserID;
    }

    /**
     * Sets the current user, !!this is the same as logging in.
     * As it requests the user and a listener on the score of the user
     * @param currentUserID the new ID of the user.
     */
    private void setCurrentUserID(int currentUserID) {
        if (currentUserID == -1) return;

        setLoggedInUser(currentUserID);

        // request the score listener for the new user and info about the new user
        String request = String.format(Locale.US, "listen_score: %d\nget_user: %d", this.currentUserID, this.currentUserID);
        new WriteToServer(request, writer, mainThreadHandler).start();
    }

    /**
     * Retrieves the server IP from the shared preferences (local storage)
     * @return The stored server IP
     */
    public String getServerIP() {
        return sharedPreferencesServer.getString("SERVER_IP", "");
    }

    /**
     * Updates the server IP in the local storage
     * And disconnects to the current server and connects to the new one
     * @param serverIP the new server IP in the form of 0.0.0.0
     */
    public void updateServerIP(String serverIP) {
        sharedPreferencesServer.edit().putString("SERVER_IP", serverIP).apply();
        server_IP = serverIP;
        reconnect();
    }

    public int getLoggedInUser() {
        return sharedPreferencesServer.getInt("USER", -1);
    }
    private void setLoggedInUser(int userID) {
        currentUserID = userID;
        sharedPreferencesServer.edit().putInt("USER", userID).apply();
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

    public class ServiceBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }
}
