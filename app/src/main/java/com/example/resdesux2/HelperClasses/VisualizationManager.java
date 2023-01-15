package com.example.resdesux2.HelperClasses;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

public class VisualizationManager {
    public static final int[][] IMAGES = {
            {R.drawable.dagoestaand1, R.drawable.dagoestaand2},
            {R.drawable.dagoestaand3, R.drawable.dagoestaand4},
            {R.drawable.dagoestaand5, R.drawable.dagoestaand6}
    };
    public static int[][] VIDEOS = {
            {R.drawable.videodagoesleep1, R.drawable.videodagoerest1, R.drawable.videodagoestaand1},
            {R.drawable.videodagoesleep3, R.drawable.videodagoerest3, R.drawable.videodagoestaand3},
            {R.drawable.videodagoesleep4, R.drawable.videodagoerest5, R.drawable.videodagoestaand5},
    };

    public static int getVideo(int intensity, int frequency) {
        intensity = clamp(intensity/2, VIDEOS.length - 1);
        frequency = clamp(frequency/2, VIDEOS[intensity].length - 1);
        return VIDEOS[intensity][frequency];
    }

    public static int getImage(User.Score score) {
        return getImage(score.getIntensityScore(), score.getFrequencyScore());
    }

    public static int getImage(int intensity, int frequency) {
        intensity = clamp(intensity, IMAGES.length - 1);
        frequency = clamp(frequency, IMAGES[intensity].length - 1);
        return IMAGES[intensity][frequency];
    }

    private static int clamp(int variable,  int max) {
        return clamp(variable, 0, max);
    }

    private static int clamp(int variable, int min, int max) {
        return Math.min(max, Math.max(variable, min));
    }
}