package com.example.pettivitywatch.communication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {
    private static final String LISTEN_PATH = "/my_path";
    private static final String TAG = "MessageService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //If the message’s path equals "/my_path"...//
        if (messageEvent.getPath().equals(LISTEN_PATH)) {
            //...retrieve the message//
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            Log.e(TAG, "onMessageReceived: " + message);
            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}