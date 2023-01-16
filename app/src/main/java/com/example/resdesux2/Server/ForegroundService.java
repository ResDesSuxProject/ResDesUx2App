package com.example.resdesux2.Server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.resdesux2.Activities.LoginActivity;
import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;

public class ForegroundService extends Service {
    public static final Uri TCP_DATA_URI = Uri.parse("content://com.example.resdesux2.Widgets");
    private static final String CHANNEL_ID = "ChannelID1";
    private static final int NOTIFICATION_ID = 2;
    private NotificationCompat.Builder notificationBuilder;


    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        Notification notification = createNotification(notificationBuilder, false);
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;  // START_REDELIVER_INTENT
    }

    protected void updateWidget(User user) {
        ContentValues values = new ContentValues();
        values.put("user", user.transformToString());

        getContentResolver().insert(TCP_DATA_URI, values);
        getContentResolver().notifyChange(TCP_DATA_URI, null);
    }

    private Notification createNotification(NotificationCompat.Builder notificationBuilder, boolean connected) {
        Intent redirectIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, redirectIntent, PendingIntent.FLAG_IMMUTABLE);
        return notificationBuilder
                .setContentText(connected ? getText(R.string.connected_message) : getText(R.string.not_connected_message))
                .setSmallIcon(R.drawable.baseline_sports_handball_24)
                .setContentIntent(pendingIntent)
                .setSilent(true)
                .setOnlyAlertOnce(true)
                .build();
    }
    protected void updateNotification(boolean connected) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, createNotification(notificationBuilder, connected));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Foreground service notification", NotificationManager.IMPORTANCE_MIN);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
