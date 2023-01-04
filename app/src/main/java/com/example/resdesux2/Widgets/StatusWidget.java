package com.example.resdesux2.Widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.example.resdesux2.Models.User;
import com.example.resdesux2.R;
import com.example.resdesux2.Server.ServerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of App Widget functionality.
 */
public class StatusWidget extends AppWidgetProvider {
    public static final Uri TCP_DATA_URI = Uri.parse("content://com.example.resdesux2.Widgets");

    private static final String TAG = "Widget";
    private RemoteViews currentView;

    // Declare the views
    private static final Map<String, Integer> views;
    static {
        Map<String, Integer> _views = new HashMap<>();
        _views.put("12", R.layout.status_widget_small);
        _views.put("13", R.layout.status_widget_small);

        _views.put("14", R.layout.status_widget_wide);
        _views.put("15", R.layout.status_widget_wide);

        _views.put("*", R.layout.status_widget);

        views = Collections.unmodifiableMap(_views);
    }

    // load the images
    private static final int[] dogImages = {
            R.drawable.dagoestaand1, R.drawable.dagoestaand2, R.drawable.dagoestaand3,
            R.drawable.dagoestaand4, R.drawable.dagoestaand5, R.drawable.dagoestaand6
    };

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        currentView = getRemoteViews(context, appWidgetManager, appWidgetId);
        fillViewWithUserData(context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, currentView);
    }

    private void fillViewWithUserData(Context context) {
        // Fill in the data fo the user
        CharSequence widgetText = context.getString(R.string.appwidget_welcome);
        User currentUser = getDataFromContentProvider(context);

        if (currentUser != null && currentUser.getID() != -1) {
            currentView.setTextViewText(R.id.appwidget_welcome, widgetText + " " + currentUser.getUserName());

            User.Score score = currentUser.getScore();

            int index = score.getIntensityScore()/3 + score.getFrequencyScore()/3;
            if (index > dogImages.length) index = dogImages.length - 1;
            else if (index < 0) index = 0;

            currentView.setImageViewResource(R.id.appwidget_image, dogImages[index]);
        } else {
            currentView.setImageViewResource(R.id.appwidget_image, R.drawable.baseline_signal_wifi_connected_no_internet_4_24_black);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        setupService(context);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void setupService(Context context) {
        // start the service
        ContextCompat.startForegroundService(context, new Intent(context, ServerService.class));

        // Register the TcpDataContentObserver to observe changes to the content URI
        context.getContentResolver().registerContentObserver(TCP_DATA_URI, true, new TcpDataContentObserver(context));
    }

    private static User getDataFromContentProvider(Context context) {
        // Query the content provider for the data
        Cursor cursor = context.getContentResolver().query(TCP_DATA_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int userColumn = cursor.getColumnIndex("user");
            String userStr = userColumn >= 0 ? cursor.getString(userColumn) : "";
            User user = User.generateFromString(userStr);
            cursor.close();
            return user != null ? user : new User(-1, "", 0, 0, 20);
        }
        return null;
    }

    /**
     * Determine appropriate view based on row or column provided.
     * @return the new Remote view of the appropriate dimension.
     */
    private RemoteViews getRemoteViews(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // See the dimensions and
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get min width and height.
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        // First find out rows and columns based on width provided.
        int rows = getCellsForSize(minHeight);
        int columns = getCellsForSize(minWidth);

        String key = rows <= 1 ? "1" + columns : "*";
        if (!views.containsKey(key) || views.get(key) == null) return new RemoteViews(context.getPackageName(), R.layout.status_widget);

       return new RemoteViews(context.getPackageName(), views.get(key));
    }

    /**
     * Returns number of cells needed for given size of the widget.
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(double size) {
        return (int) Math.round((size + 30.0) / 70.0);
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