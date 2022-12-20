package com.example.resdesux2.Server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class BoundActivity extends AppCompatActivity {
    protected ServerService serverService;
    protected boolean isBound;

    /**
     * This instantiate a connection with the service and handles that it connects.
     */
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

    /**
     * Called when activity is started, after onCreate.
     * This connects and if needed start the Service.
     */
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

    /**
     * This method is called once the server service is bound and connected to the server,
     * You can override it and put all your server related calls here.
     * @param connected This parameter can be neglected as it should always be true.
     */
    protected void onConnected(boolean connected) {

    }

    /**
     * This method will be called once the activity has been bound to the server service.
     */
    private void onBound() {
        serverService.setConnectionListener(this::onConnected);
    }

    /**
     * Called when the activity stopped, it disconnects the service.
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
