package com.example.resdesux2.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import com.daimajia.numberprogressbar.NumberProgressBar;
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

    public void setIndeterminate (boolean indeterminate){

    }

    @Override
    protected void onScoreChange(int intensityScore, int frequencyScore) {
        super.onScoreChange(intensityScore, frequencyScore);
        NumberProgressBar bar_intensity = findViewById(R.id.determinateBar_intensity);
        NumberProgressBar bar_frequency = findViewById(R.id.determinateBar_frequency);
        bar_intensity.setProgress(intensityScore);
        bar_frequency.setProgress(frequencyScore);



    }
}