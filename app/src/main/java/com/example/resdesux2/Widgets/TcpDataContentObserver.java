package com.example.resdesux2.Widgets;

import android.content.Context;
import android.database.ContentObserver;

public class TcpDataContentObserver extends ContentObserver {
    private Context context;

    public TcpDataContentObserver(Context context) {
        super(null);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // Update the widget when the content URI changes
        StatusWidget.updateWidget(context);
    }
}
