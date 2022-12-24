package com.example.resdesux2.HelperClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

import java.util.ArrayList;
import java.util.Locale;

public class UserListAdaptor extends ArrayAdapter<User> {
    private static final int[] dogsImages = {
            R.drawable.dagoestaand1, R.drawable.dagoestaand2, R.drawable.dagoestaand3,
            R.drawable.dagoestaand4, R.drawable.dagoestaand5, R.drawable.dagoestaand6
    };
    public UserListAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // ConvertView which is recyclable view, which is the view before it was scrolled,
        // So checking if it already inflated then before inflating it makes it faster
        View currentItemView = convertView;
  
        // If the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        }
  
        // Get the position of the view from the ArrayAdapter
        User currentUser = getItem(position);
  
        // Then get the user name and fill it in.
        TextView textViewUserName = currentItemView.findViewById(R.id.textViewUserName);
        textViewUserName.setText(currentUser.getUserName());
  
        // And the same for the score
        TextView textViewUserScore = currentItemView.findViewById(R.id.textViewUserScore);
        textViewUserScore.setText(String.format(Locale.getDefault(), "Score: %.2f", currentUser.getScore()));

        // Fill the image with the correct Dog
        ImageView doggoState = currentItemView.findViewById(R.id.imageIcon);
        int index = (int) (currentUser.getScore() / 10);
        index = Math.max(0, index);
        index = Math.min(dogsImages.length-1, index);
        doggoState.setImageResource(dogsImages[index]);

        // Then return the recyclable view
        return currentItemView;
    }
}
