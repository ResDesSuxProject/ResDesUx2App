package com.example.resdesux2.Server;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.resdesux2.Fragments.ConnectionDialog;
import com.example.resdesux2.R;
import com.example.resdesux2.Widgets.StatusWidget;
import com.example.resdesux2.Widgets.UpdateWidgetService;

public class BoundActivity extends AppCompatActivity implements ConnectionDialog.ConnectionDialogListener {
    private static final String TAG = "BoundActivity";
    protected ServerService serverService;
    protected boolean isBound;
    protected boolean isConnected;
    public Menu toolbarMenu;
    private ConnectionDialog connectionDialog;
    private boolean failedConnection;

    /**
     * This instantiate a connection with the service and handles that it connects.
     */
    private final ServiceConnection connection = new ServiceConnection() {
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
        isConnected = false;
        connectionDialog = new ConnectionDialog();

        Intent startServerServiceIntent = new Intent(this, ServerService.class);

        // The server service is created if it wasn't before
        if (!isBound) {
            startService(startServerServiceIntent);
        }

        bindService(startServerServiceIntent, connection, Context.BIND_AUTO_CREATE);

        // Check if the widget is still running, if not update it. Only needed for development
        if (!isServiceRunning(this, UpdateWidgetService.class)) {
            Intent widgetIntent = new Intent(this, StatusWidget.class);
            widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            int[] ids = AppWidgetManager.getInstance(getApplication())
                    .getAppWidgetIds(new ComponentName(getApplication(), StatusWidget.class));
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(widgetIntent);
        }
    }

    /**
     * This method will be called once the activity has been bound to the server service.
     */
    private void onBound() {
        serverService.setConnectionListener(this::onConnected);
        serverService.setConnectionFailedListener(this::onFailedConnection);
    }

    /**
     * This method is called once the server service is bound and connected to the server,
     * You can override it and put all your server related calls here.
     * @param connected This parameter can be neglected as it should always be true.
     */
    protected void onConnected(boolean connected) {
        isConnected = true;
        failedConnection = false;
        invalidateOptionsMenu();
    }
    /**
     * called from the server service when the connect task failed to connect to the server
     * @param isConnected if the server is connected, always false
     */
    public void onFailedConnection(boolean isConnected) {
        failedConnection = true;
        invalidateOptionsMenu();
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

    /**
     * adds an option menu to the toolbar
     * @param menu The options menu in which you place your items.
     *
     * @return if it was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbarMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Called when a item on the option menu / toolbar has been pressed
     * @param item The menu item that was selected.
     * @return if the pressed option was handled
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.not_connected_toolbar) {
            connectionDialog.show(getSupportFragmentManager(), "ConnectionDialog");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Toggles the not connected symbol based on the failed connection status
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     *
     * @return idk
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.not_connected_toolbar).setVisible(failedConnection);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Used to communicate with the connection dialog.
     * It is send from the dialog and is used to update the ip and reconnect to the new server
     * @param dialog the dialog that calls it
     * @param serverIP the new and updated Server IP
     */
    @Override
    public void onDialogServerIPUpdated(DialogFragment dialog, String serverIP) {
        if (isBound) serverService.updateServerIP(serverIP);
    }

    /**
     * Returns the server IP or an empty string if it isn't connected to the server service
     * @return the server ip from local storage or "" if not connected
     */
    public String getServerIP() {
        return isBound ? serverService.getServerIP() : "";
    }

    /**
     * Test is a service is already running
     * @param context the context in which it should be tested
     * @param serviceClass the class of the service in question
     * @return true or false depending if the service is running
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
