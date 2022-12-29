package com.example.resdesux2.Widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.resdesux2.R;

/**
 * Implementation of App Widget functionality.
 */
public class StatusWidget extends AppWidgetProvider {
    public static final Uri TCP_DATA_URI = Uri.parse("content://com.example.resdesux2.Widgets");

    private static final String TAG = "Widget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.welcome_message);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.status_widget);

        String txt = getDataFromContentProvider(context);

        Log.i(TAG, "updateWidget: " + txt);
        views.setTextViewText(R.id.appwidget_text, widgetText + " " + txt);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(TAG, "onUpdate: Doei");

        // start the service
        context.startService(new Intent(context, UpdateWidgetService.class));

        // Register the TcpDataContentObserver to observe changes to the content URI
        context.getContentResolver().registerContentObserver(TCP_DATA_URI, true, new TcpDataContentObserver(context));

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateWidget(Context context) {
        // Update the widget with the received data
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StatusWidget.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static String getDataFromContentProvider(Context context) {
        // Query the content provider for the data
        Cursor cursor = context.getContentResolver().query(TCP_DATA_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int dataColumn = cursor.getColumnIndex("data");
            String data = "";
            if (dataColumn > -1) {
                data = cursor.getString(dataColumn);
            }
            cursor.close();
            return data;
        }
        return null;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}