package com.example.resdesux2.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

import java.util.ArrayList;
import java.util.Locale;

public class UserListAdaptor extends ArrayAdapter<User> {
    public UserListAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // convertView which is recyclable view
        View currentItemView = convertView;
  
        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }
  
        // get the position of the view from the ArrayAdapter
        User currentUser = getItem(position);
  
        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textViewUserName = currentItemView.findViewById(R.id.textViewUserName);
        textViewUserName.setText(currentUser.getUserName());
  
        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textViewUserScore = currentItemView.findViewById(R.id.textViewUserScore);
        textViewUserScore.setText(String.format(Locale.getDefault(), "Score: %.2f", currentUser.getScore()));
  
        // then return the recyclable view
        return currentItemView;
    }
}
