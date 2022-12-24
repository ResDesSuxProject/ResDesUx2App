package com.example.resdesux2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

public class login extends BoundActivity {

    private static final String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_login).setOnClickListener(this::loginClicked); // VAN main NAAR calendar
    }

    public void loginClicked(View view) {
        if (isBound) {  // isConnected &&
            String username = ((TextView) findViewById(R.id.editTextTextPersonName)).getText().toString();

            Log.d(TAG, "loginClicked: " + username);
            String password = "";
            serverService.login(username, password, this::loggedIn);
//        } else {
//            // TODO show server not connected
//            loggedIn(0);
        }
    }

    public void loggedIn(int userID) {
        if (userID != -1) {
            Intent intent_login = new Intent(login.this, MainActivity.class);
            startActivity(intent_login);
        } else {
            // TODO show error message
        }
    }
}