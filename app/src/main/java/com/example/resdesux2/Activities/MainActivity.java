package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.Server.BoundActivity;
import com.example.resdesux2.R;

import org.w3c.dom.Text;

public class MainActivity extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // proberen
        findViewById(R.id.button_friends).setOnClickListener(buttonClickListener);  //VAN main NAAR friends
        findViewById(R.id.button_calendar).setOnClickListener(buttonClickListener); // VAN main NAAR calendar


        // proberen
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_friends:
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    break;
                case R.id.button_calendar:
                    Intent intent_calendar = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent_calendar);
                    break;
            }
        }
    };

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Example of how you can listen to a score change
        serverService.setScoreListener(this::onScoreChange);
        serverService.getCurrentUser(this::onUserRetrieved);
    }

    public void onScoreChange(double value) {
        TextView textView = (TextView) findViewById(R.id.scoreView);
        textView.setText(String.format("Score: %s", value));
    }

    public void onUserRetrieved(User currentUser) {
        ((TextView) findViewById(R.id.main_welcome_message)).setText("Hello " + currentUser.getUserName());
    }



    // hieronder is oud
//    public void sendMessage(View view) {
//        Intent intent = new Intent(MainActivity.this, Dashboard.class);
//        startActivity(intent);
//    }
//    public void sendMessage(View view) {
//        Intent intent = new Intent(MainActivity.this, calendar.class);
//        startActivity(intent);
//    }
}