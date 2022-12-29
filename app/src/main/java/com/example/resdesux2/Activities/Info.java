package com.example.resdesux2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.resdesux2.R;

public class Info extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // Assigns a click listener to the button
        findViewById(R.id.button_info_to_main).setOnClickListener(this::InfoBackBtnClicked);
    }

    private void InfoBackBtnClicked(View view) {
        // start the dashboard activity and navigate to it
        Intent intent = new Intent(Info.this, MainActivity.class);
        startActivity(intent);
    }
}