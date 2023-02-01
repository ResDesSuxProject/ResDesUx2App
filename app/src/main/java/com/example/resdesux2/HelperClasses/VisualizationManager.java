package com.example.resdesux2.HelperClasses;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

public class VisualizationManager {
    public static final int[][] IMAGES = {
            {R.drawable.dagoefoto1_1, R.drawable.dagoefoto2_1, R.drawable.dagoefoto3_1},
            {R.drawable.dagoefoto1_2, R.drawable.dagoefoto2_2, R.drawable.dagoefoto3_2},
            {R.drawable.dagoefoto1_3, R.drawable.dagoefoto2_3, R.drawable.dagoefoto3_3},
    };
    public static int[][] VIDEOS = {
            {R.drawable.dagoe1_1, R.drawable.dagoe2_1, R.drawable.dagoe3_1},
            {R.drawable.dagoe1_2, R.drawable.dagoe2_2, R.drawable.dagoe3_2},
            {R.drawable.dagoe1_3, R.drawable.dagoe2_3, R.drawable.dagoe3_3},
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
        intensity = clamp(intensity/2, IMAGES.length - 1);
        frequency = clamp(frequency/2, IMAGES[intensity].length - 1);
        return IMAGES[intensity][frequency];
    }

    private static int clamp(int variable,  int max) {
        return clamp(variable, 0, max);
    }

    private static int clamp(int variable, int min, int max) {
        return Math.min(max, Math.max(variable, min));
    }
}
