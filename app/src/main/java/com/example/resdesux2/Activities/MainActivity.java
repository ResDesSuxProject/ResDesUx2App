package com.example.resdesux2.Activities;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.resdesux2.HelperClasses.VisualizationManager;
import com.example.resdesux2.Models.User;
import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BoundActivity {
//https://developers.google.com/fit/android/get-started#groovy-dsl

    protected Handler myHandler;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    public static final String PATH = "/heartRates";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationFragment navigationFragment = NavigationFragment.newInstance(NavigationFragment.Options.Home, this::navigateTo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigationFragmentContainer, navigationFragment).commit();

        myHandler = new Handler(message -> {
            Bundle stuff = message.getData();
            Log.e("Test", "handleMessage: " + stuff.getString("messageText"));
            return true;
        });

        //Register to receive local broadcasts, which we'll be creating in the next step//
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        findViewById(R.id.my_image_view).setOnClickListener(this::talkClick);
//        WatchSensor watchSensor = new WatchSensor(this);
//        ActiveWatchSensor activeWatchSensor = new ActiveWatchSensor(this);
//        GoogleSignInOptionsExtension fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//                        .build();
//
//        GoogleSignInAccount googleSignInAccount =
//                GoogleSignIn.getAccountForExtension(this, fitnessOptions);
////
//        if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
//            GoogleSignIn.requestPermissions(
//                    this, // your activity
//                    1, // e.g. 1
//                    googleSignInAccount,
//                    fitnessOptions);
//        } else {
//            KotlinWatch kotlinWatch = new KotlinWatch(this, this);
//            new Thread(kotlinWatch::run).start();
//
//
//            Date currentTime = Calendar.getInstance().getTime();
//            Task<DataReadResponse> response = Fitness.getHistoryClient(this, googleSignInAccount)
//                    .readData(new DataReadRequest.Builder()
//                            .read(DataType.TYPE_HEART_RATE_BPM)
//                            .setTimeRange(currentTime.getTime() - 1000 * 3600 * 24, currentTime.getTime() + 1000 * 3600 * 24, TimeUnit.MILLISECONDS)
//                            .build());
//            new Thread(() -> {
//                DataReadResponse readDataResponse = null;
//                try {
//                    readDataResponse = Tasks.await(response);
//                    DataSet dataSet = readDataResponse.getDataSet(DataType.TYPE_HEART_RATE_BPM);
//                    Log.e("Yess", "onCreate: " + dataSet.getDataPoints());
//                } catch (ExecutionException | InterruptedException e) {
//                    Log.e("Yess", "onCreate: " + e);
//                }
//            }).start();
//        }

        // Samples the user's activity once per minute.
//        Task<Void> response = Fitness.getRecordingClient(this, googleSignInAccount)
//                .subscribe(DataType.TYPE_HEART_RATE_BPM);
    }

    //Define a nested class that extends BroadcastReceiver//
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Upon receiving each message from the wearable, display the following text//
            String message = "I just received a message from the wearable " + receivedMessageNumber++;
            Log.i("Test", "onReceive: " + message);
        }
    }


    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Example of how you can listen to a score change
        serverService.getCurrentUser(this::onUserRetrieved);
    }

    @Override
    protected void onScoreChange(int intensityScore, int frequencyScore) {
        super.onScoreChange(intensityScore, frequencyScore);
        if (isDestroyed) return;

        // Show gif with rounded corners and fade
        ImageView imageView = findViewById(R.id.my_image_view);
        Glide.with(this)
                .load(VisualizationManager.getVideo(intensityScore, frequencyScore))
                .transition(withCrossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .into(imageView);
    }

    private void onUserRetrieved(User currentUser) {
        ((TextView) findViewById(R.id.main_welcome_message)).setText("Hello " + currentUser.getUserName());
    }

    public void talkClick(View v) {
        String message = "Sending message.... ";
        Log.i("Test", "talkClick: Sending message");
        //Sending a message can block the main UI thread, so use a new thread//
        new NewThread(PATH, message).start();
    }


    //Use a Bundle to encapsulate our message//
    public void sendmessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }

    class NewThread extends Thread {
        String path;
        String message;

        //Constructor for sending information to the Data Layer//
        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            //Retrieve the connected devices, known as nodes//
            Task<List<Node>> wearableList =
            Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                    //Send the message//
                    Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        //Block on a task and get the result synchronously//
                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage("I just sent the wearable a message " + sentMessageNumber++);
                        //if the Task fails, then…..//
                    } catch (ExecutionException | InterruptedException exception) {
                        //TO DO: Handle the exception//
                    }
                }
            } catch (ExecutionException | InterruptedException exception) {
                //TO DO: Handle the exception//
            }
        }
    }
}