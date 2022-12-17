package com.example.resdesux2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(intent);
                    break;
                case R.id.button_calendar:
                    Intent intent_calendar = new Intent(MainActivity.this, calendar.class);
                    startActivity(intent_calendar);
                    break;
            }
        }
    };


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