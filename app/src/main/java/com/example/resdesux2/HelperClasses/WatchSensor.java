package com.example.resdesux2.HelperClasses;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static com.google.android.gms.common.util.CollectionUtils.setOf;

import android.content.Context;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.PermissionController;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.Record;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WatchSensor{

    private static final String TAG = "WatchSensor";
    private boolean connected;
    private GoogleSignInAccount googleSignInAccount;

    public WatchSensor(AppCompatActivity activity) {
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                        .build();

        googleSignInAccount = GoogleSignIn.getAccountForExtension(activity, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    1, // e.g. 1
                    googleSignInAccount,
                    fitnessOptions);
        } else {
            requestData(activity);
        }
    }

    public void requestData(AppCompatActivity activity) {
        Date currentTime = Calendar.getInstance().getTime();
        Task<DataReadResponse> response = Fitness.getHistoryClient(activity, googleSignInAccount)
                .readData(new DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM)
                        .setTimeRange(currentTime.getTime() - 1000 * 3600 * 24, currentTime.getTime(), TimeUnit.MILLISECONDS)
                        .build());

        new Thread(() -> {
            DataReadResponse readDataResponse = null;
            try {
                readDataResponse = Tasks.await(response);
                DataSet dataSet = readDataResponse.getDataSet(DataType.TYPE_HEART_RATE_BPM);
                dataSet.getDataPoints().forEach(dataPoint -> {
                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value value = dataPoint.getValue(field);
                        Log.i(TAG, dataPoint.getTimestamp(TimeUnit.DAYS) % 365 + " ." + (dataPoint.getTimestamp(TimeUnit.HOURS) % 24 + 1) + "." + (dataPoint.getTimestamp(TimeUnit.MINUTES) % 60) + " Detected DataPoint: " + value + " " + field.getName());
                    }
                });

            } catch (ExecutionException | InterruptedException e) {
                Log.e("Yess", "onCreate: " + e);
            }
        }).start();
    }


    public void destroy() {
//        passiveMonitoringClient.clearPassiveListenerCallbackAsync();
    }
}
