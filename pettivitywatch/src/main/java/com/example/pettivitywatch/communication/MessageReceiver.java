package com.example.pettivitywatch.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pettivitywatch.models.ChangeListener;
import com.example.pettivitywatch.models.User;


/**
 * Receives the data from the messageService
 */
public class MessageReceiver extends BroadcastReceiver {
    private final ChangeListener<String> listener;
    private final ChangeListener<User.Score> scoreListener;

    public MessageReceiver(ChangeListener<String> listener, ChangeListener<User.Score> scoreListener) {
        this.listener = listener;
        this.scoreListener = scoreListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Upon receiving each message from the wearable send it on to the listener
        String message = intent.getExtras().getString("message");
        if (message.contains("score:")) {
            scoreListener.onChange(new User.Score(
                    intent.getExtras().getInt(MessageService.INTENSITY_KEY),
                    intent.getExtras().getInt(MessageService.FREQUENCY_KEY)));
        }
        listener.onChange(message);
    }
}
