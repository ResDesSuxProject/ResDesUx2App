package com.example.resdesux2.Widgets;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class UpdateWidgetService extends Service {
    public static final Uri TCP_DATA_URI = Uri.parse("content://com.example.resdesux2.Widgets");

    public int onStartCommand(Intent intent, int flags, int startId) {
        // Receive data from the TCP server and send a broadcast to update the widget
        String[] data = {"Frank", "Sil", "Henk", "User", "Floor", "Jochem"};

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                ContentValues values = new ContentValues();
                values.put("data", data[(int) Math.floor(Math.random() * data.length)]);
                getContentResolver().insert(TCP_DATA_URI, values);
                getContentResolver().notifyChange(TCP_DATA_URI, null);

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);

        return START_STICKY;  // START_REDELIVER_INTENT
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
