package com.example.resdesux2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

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