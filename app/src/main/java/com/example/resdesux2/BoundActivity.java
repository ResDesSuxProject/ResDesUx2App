package com.example.resdesux2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resdesux2.Services.ServerService;

public class BoundActivity extends AppCompatActivity {
    protected ServerService serverService;
    protected boolean isBound;

    protected final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ServerService.ServiceBinder binder = (ServerService.ServiceBinder) service;
            serverService = binder.getService();
            isBound = true;
            onBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ServerService.class);

        // The server service is created if it wasn't before
        if (!isBound) {
            startService(intent);
        }

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    protected void onConnected(boolean connected) {

    }

    private void onBound() {
        serverService.setConnectionListener(this::onConnected);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
