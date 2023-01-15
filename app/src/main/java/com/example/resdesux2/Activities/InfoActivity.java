package com.example.resdesux2.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

public class InfoActivity extends BoundActivity {

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