package com.example.pettivitywatch.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pettivitywatch.models.User;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {
    private static final String LISTEN_PATH = "/pettivity_userdata";
    private static final String TAG = "MessageService";
    public static final String INTENSITY_KEY = "intensity";
    public static final String FREQUENCY_KEY = "frequency";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(LISTEN_PATH)) {
            // ...retrieve the message
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);

            if (message.contains("score")) {
                User.Score score = convertToScore(message);
                if (score != null) {
                    messageIntent.putExtra(INTENSITY_KEY, score.getIntensityScore());
                    messageIntent.putExtra(FREQUENCY_KEY, score.getFrequencyScore());
                }
            }

            messageIntent.putExtra("message", message);
            Log.v(TAG, "onMessageReceived: " + message);
            // Broadcast the received Data Layer messages locally
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    private User.Score convertToScore(String line){
        String arguments = line.split(":")[1].trim();
        String[] scores = arguments.split(",");

        try {
            int _score = scores.length >= 2 ? Integer.parseInt(scores[0]) : (int) Double.parseDouble(scores[0]);
            return new User.Score(_score, scores.length >= 2 ? Integer.parseInt(scores[1]) : _score);
        } catch (NumberFormatException e) {
            Log.e(TAG, "convertToScore: " + e.getMessage());
            return null;
        }
    }
}