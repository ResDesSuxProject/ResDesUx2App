package com.example.resdesux2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.Server.BoundActivity;
import com.example.resdesux2.HelperClasses.UserListAdaptor;
import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

import java.util.ArrayList;

public class DashboardActivity extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        NavigationFragment navigationFragment = NavigationFragment.newInstance(NavigationFragment.Options.Friends, this::navigateTo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigationFragmentContainer, navigationFragment).commit();
    }

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Once the server has been connected request the users
        serverService.getUsers(this::populateList);
    }

    private void populateList(ArrayList<User> users) {
        // Create an ArrayAdapter to manage the data for the ListView
        UserListAdaptor userListAdaptor = new UserListAdaptor(this, android.R.layout.simple_list_item_1, users);

        // Find the ListView widget in the layout
        ListView listView = findViewById(R.id.users_list);

        // Set the ArrayAdapter as the adapter for the ListView
        listView.setAdapter(userListAdaptor);
    }
}