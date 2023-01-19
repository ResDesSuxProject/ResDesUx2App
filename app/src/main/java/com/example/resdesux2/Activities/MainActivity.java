package com.example.resdesux2.Activities;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.resdesux2.HelperClasses.KotlinWatch;
import com.example.resdesux2.HelperClasses.VisualizationManager;
import com.example.resdesux2.Models.User;
import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.Locale;

public class MainActivity extends BoundActivity {
//https://developers.google.com/fit/android/get-started#groovy-dsl
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationFragment navigationFragment = NavigationFragment.newInstance(NavigationFragment.Options.Home, this::navigateTo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigationFragmentContainer, navigationFragment).commit();

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                        .build();

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(this, fitnessOptions);
//
        if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    1, // e.g. 1
                    googleSignInAccount,
                    fitnessOptions);
        } else {
            KotlinWatch kotlinWatch = new KotlinWatch(this, this);
            new Thread(kotlinWatch::run).start();
        }
//        } else {
//            Date currentTime = Calendar.getInstance().getTime();
//
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
        TextView textView = (TextView) findViewById(R.id.scoreView);
        textView.setText(String.format(Locale.US, "Your scores are %d and %d",
                intensityScore, frequencyScore));

        // Show gif with rounded corners and fade
        ImageView imageView = findViewById(R.id.my_image_view);
        Glide.with(this)
                .load(VisualizationManager.getVideo(intensityScore, frequencyScore))
                .transform(new CenterCrop(),new RoundedCorners(14))
                .transition(withCrossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .into(imageView);
    }

    private void onUserRetrieved(User currentUser) {
        ((TextView) findViewById(R.id.main_welcome_message)).setText("Hello " + currentUser.getUserName());
    }
}