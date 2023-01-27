package com.example.resdesux2.Communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.resdesux2.Models.ChangeListener;

/**
 * Receives the data from the messageService
 */
public class MessageReceiver extends BroadcastReceiver {
    private final ChangeListener<String> listener;

    public MessageReceiver(ChangeListener<String> listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Upon receiving each message from the wearable send it on to the listener
        String message = intent.getExtras().getString("message");
        Log.v("MessageReceiver", message);
        listener.onChange(message);
    }
}
