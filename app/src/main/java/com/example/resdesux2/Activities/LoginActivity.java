package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

public class LoginActivity extends BoundActivity {

    private static final String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.button_login).setOnClickListener(this::loginClicked); // VAN main NAAR calendar
    }

    public void loginClicked(View view) {
        if (isBound && isConnected) {  // isConnected &&
            String username = ((TextView) findViewById(R.id.editTextTextPersonName)).getText().toString();

            Log.d(TAG, "loginClicked: " + username);
            String password = "";
            serverService.login(username, password, this::loggedIn);
        } else {
            Toast.makeText(this, "Server not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void loggedIn(int userID) {
        if (userID != -1) {
            Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent_login);
        } else {
            Toast.makeText(this, "Username incorrect", Toast.LENGTH_SHORT).show();
        }
    }
}