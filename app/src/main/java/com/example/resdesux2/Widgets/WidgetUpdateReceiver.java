package com.example.resdesux2.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "WidgetUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//        // Get the data from the intent
//        String data = intent.getStringExtra("data");
//        Log.e(TAG, "onReceive: Data: " + data);
//
//        // Update the widget with the received data
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
//        StatusWidget.updateWidget(context, appWidgetManager, appWidgetIds, data);
    }


}

