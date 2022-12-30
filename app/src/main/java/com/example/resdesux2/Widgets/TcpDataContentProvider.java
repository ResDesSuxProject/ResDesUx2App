package com.example.resdesux2.Widgets;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.example.resdesux2.Models.User;

public class TcpDataContentProvider extends ContentProvider {
    private static final Uri TCP_DATA_URI = Uri.parse("content://com.example.resdesux2.Widgets");
    private String userStr;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Return a Cursor with the data stored in the content provider
        MatrixCursor cursor = new MatrixCursor(new String[]{"user"});
        cursor.addRow(new Object[]{userStr});

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Store the data in the content provider
        userStr = values.getAsString("user");
        return TCP_DATA_URI;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Not implemented
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Not implemented
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // Not implemented
        return null;
    }
}
