package com.example.resdesux2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.resdesux2.Activities.MainActivity;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_login).setOnClickListener(buttonClickListener); // VAN main NAAR calendar

    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_login:
                    Intent intent_login = new Intent(login.this, MainActivity.class);
                    startActivity(intent_login);
                    break;
            }
        }
    };
}