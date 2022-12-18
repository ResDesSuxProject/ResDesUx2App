package com.example.resdesux2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.example.resdesux2.Services.ServerService;

public class Dashboard extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.button_friends_back_to_main).setOnClickListener(buttonClickListener);  //VAN main NAAR friends


    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_friends_back_to_main:
                    Intent intent_friends_back_to_main = new Intent(Dashboard.this, MainActivity.class);
                    startActivity(intent_friends_back_to_main);
                    break;
            }
        }
    };
}