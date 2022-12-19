package com.example.resdesux2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.resdesux2.Services.ServerService;

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

    @Override
    protected void onStart() {
        super.onStart();

        // The server service is created if it wasn't before
        if (!isBound) {
            Intent intent = new Intent(this, ServerService.class);
            startService(intent);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
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