package com.example.resdesux2.HelperClasses;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

public class ActiveWatchSensor {
    private static final String TAG = "ActiveWatchSensor";

    public ActiveWatchSensor(AppCompatActivity activity) {
//        FitnessOptions fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_HEART_RATE_BPM).build();

//        printList(activity, fitnessOptions);
//        addListener(activity, fitnessOptions);
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .build();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getAccountForExtension(activity, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    1, // e.g. 1
                    googleSignInAccount,
                    fitnessOptions);
        } else {
            addStepListener(activity);
        }
    }

    public void printList(AppCompatActivity activity, FitnessOptions fitnessOptions) {
// Note: Fitness.SensorsApi.findDataSources() requires the
// ACCESS_FINE_LOCATION permission.
        Fitness.getSensorsClient(activity.getApplicationContext(), GoogleSignIn.getAccountForExtension(activity.getApplicationContext(), fitnessOptions))
                .findDataSources(
                        new DataSourcesRequest.Builder()
                                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                                .setDataSourceTypes(DataSource.TYPE_RAW)
                                .build())
                .addOnSuccessListener(dataSources -> dataSources.forEach(dataSource -> {
                    Log.i(TAG, "Data source found: ${it.streamIdentifier}");
                    Log.i(TAG, "Data Source type: ${it.dataType.name}");

                    if (dataSource.getDataType() == DataType.TYPE_HEART_RATE_BPM) {
                        Log.i(TAG, "Data source for TYPE_HEART_RATE_BPM found!");
                    }
                }))
                .addOnFailureListener(e -> Log.e(TAG, "Find data sources request failed", e));

        Log.i(TAG, "ActiveWatchSensor: hi");
    }

    private void addListener(AppCompatActivity activity, FitnessOptions fitnessOptions) {
        OnDataPointListener listener = dataPoint -> {
            for (Field field : dataPoint.getDataType().getFields()) {
                Value value = dataPoint.getValue(field);
                Log.i(TAG, "Detected DataPoint field: ${field.getName()}");
                Log.i(TAG, "Detected DataPoint value: $value");
            }
        };
        Fitness.getSensorsClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
                .add(new SensorRequest.Builder()
//                            .setDataSource(dataSource) // Optional but recommended
                            // for custom data sets.
                            .setDataType(DataType.TYPE_HEART_RATE_BPM) // Can't be omitted.
                            .setSamplingRate(10, TimeUnit.SECONDS)
                            .build(),
                        listener
                )
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Listener registered!"))
                .addOnFailureListener(task ->
                        Log.e(TAG, "Listener not registered." + task.getMessage(), task.getCause()));
    }

    public void addStepListener(AppCompatActivity activity) {
        FitnessOptions fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_DELTA).build();

        OnDataPointListener listener = dataPoint -> {
            for (Field field : dataPoint.getDataType().getFields()) {
                Value value = dataPoint.getValue(field);
                Log.i(TAG, "Detected DataPoint field: ${field.getName()}");
                Log.i(TAG, "Detected DataPoint value: $value");
            }
        };
        Fitness.getSensorsClient(activity, GoogleSignIn.getAccountForExtension(activity, fitnessOptions))
                .add(
                        new SensorRequest.Builder()
                                // for custom data sets.
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                                .setSamplingRate(10, TimeUnit.SECONDS)
                                .build(),
                        listener
                )
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Listener registered!"))
                .addOnFailureListener(task ->
                        Log.e(TAG, "Listener not registered.", task.getCause()));
    }
}
