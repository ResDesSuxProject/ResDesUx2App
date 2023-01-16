package com.example.resdesux2.Activities;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.resdesux2.HelperClasses.VisualizationManager;
import com.example.resdesux2.Models.User;
import com.example.resdesux2.NavigationFragment;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.BoundActivity;

import java.util.Locale;

public class MainActivity extends BoundActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationFragment navigationFragment = NavigationFragment.newInstance(NavigationFragment.Options.Home, this::navigateTo);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigationFragmentContainer, navigationFragment).commit();
    }

    @Override
    protected void onConnected(boolean connected) {
        super.onConnected(connected);

        // Example of how you can listen to a score change
        serverService.getCurrentUser(this::onUserRetrieved);
    }

    @Override
    protected void onScoreChange(int intensityScore, int frequencyScore) {
        super.onScoreChange(intensityScore, frequencyScore);
        if (isDestroyed) return;
        TextView textView = (TextView) findViewById(R.id.scoreView);
        textView.setText(String.format(Locale.US, "Your scores are %d and %d",
                intensityScore, frequencyScore));

        // Show gif with rounded corners and fade
        ImageView imageView = findViewById(R.id.my_image_view);
        Glide.with(this)
                .load(VisualizationManager.getVideo(intensityScore, frequencyScore))
                .transform(new CenterCrop(),new RoundedCorners(14))
                .transition(withCrossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .into(imageView);
    }

    private void onUserRetrieved(User currentUser) {
        ((TextView) findViewById(R.id.main_welcome_message)).setText("Hello " + currentUser.getUserName());
    }
}