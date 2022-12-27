package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

import java.util.Locale;

public class MainActivity extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assigns a click listener to the button
        findViewById(R.id.button_friends).setOnClickListener(this::friendsBtnClicked);
    }

    /**
     * Called when the button friends has been clicked.
     * It will navigate to the dashboard activity
     */
    private void friendsBtnClicked(View view) {
        // start the dashboard activity and navigate to it
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Example of how you can listen to a score change
        serverService.setScoreListener(this::onScoreChange);
        serverService.getCurrentUser(this::onUserRetrieved);
    }

    private void onScoreChange(int intensityScore, int frequencyScore) {
        TextView textView = (TextView) findViewById(R.id.scoreView);
        textView.setText(String.format(Locale.US, "Your scores are %d and %d",
                intensityScore, frequencyScore));
    }

    private void onUserRetrieved(User currentUser) {
        ((TextView) findViewById(R.id.main_welcome_message)).setText("Hello " + currentUser.getUserName());
    }
}