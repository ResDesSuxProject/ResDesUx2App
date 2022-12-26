package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
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

        findViewById(R.id.login_button).setOnClickListener(this::loginClicked);
    }

    /**
     * This function is called when the login button is called and handles the interaction with it
     * @param view the view connected to the button
     */
    public void loginClicked(View view) {
        if (isBound && isConnected) {  // isConnected &&
            String username = ((TextView) findViewById(R.id.login_username_input)).getText().toString().trim();
            String password = ((TextView) findViewById(R.id.login_password_input)).getText().toString().trim();

            if (username.length() == 0) {
                ((TextView) findViewById(R.id.login_username_input)).setError("Username is empty");
                return;
            }

            serverService.login(username, password, this::loggedIn);
        } else {
            Toast.makeText(this, "Server not connected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when the server responed on the login request
     * userID of -1 means that it failed, anything else is the userID of the user
     * @param userID The users ID, -1 means failed to login.
     */
    public void loggedIn(int userID) {
        if (userID != -1) {
            Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent_login);
        } else {
            Toast.makeText(this, "Username is incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);
        findViewById(R.id.login_button).setEnabled(true);
        findViewById(R.id.login_not_connected_txt).setVisibility(View.GONE);
    }

    @Override
    public void onFailedConnection(boolean isConnected) {
        super.onFailedConnection(isConnected);
        findViewById(R.id.login_button).setEnabled(false);
        findViewById(R.id.login_not_connected_txt).setVisibility(View.VISIBLE);
    }
}