package com.example.resdesux2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

public class Info extends BoundActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        NavigationFragment navigationFragment = NavigationFragment.newInstance(NavigationFragment.Options.Insight, this::navigateTo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigationFragmentContainer, navigationFragment).commit();
    }
}