package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.resdesux2.Server.BoundActivity;
import com.example.resdesux2.R;

public class MainActivity extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // proberen
        findViewById(R.id.button_friends).setOnClickListener(buttonClickListener);  //VAN main NAAR friends


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
            }
        }
    };

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Example of how you can listen to a score change
        serverService.setScoreListener(this::onScoreChange);
    }

    public void onScoreChange(double value) {
        Log.i("MainActivity", "onScoreChange: " + value);
        TextView textView = (TextView) findViewById(R.id.scoreView);
        textView.setText(String.format("Score: %s", value));
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