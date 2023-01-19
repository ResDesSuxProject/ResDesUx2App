package com.example.resdesux2.HelperClasses;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static com.google.android.gms.common.util.CollectionUtils.setOf;

import android.content.Context;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.PermissionController;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.Record;
import androidx.health.connect.client.records.StepsRecord;
import androidx.health.connect.client.request.ReadRecordsRequest;
import androidx.health.connect.client.time.TimeRangeFilter;

import com.google.android.gms.fitness.data.HealthDataTypes;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WatchSensor{

    private static final String TAG = "WatchSensor";
    private boolean connected;


    public WatchSensor(Context context) {
        if (!HealthConnectClient.isAvailable(context)) {
            connected = false;
            return;
        }
        // Health Connect is available and installed.
        HealthConnectClient healthConnectClient = HealthConnectClient.getOrCreate(context);
        connected = true;


    }

//    void readStepsByTimeRange(HealthConnectClient healthConnectClient, Instant startTime, Instant endTime) {
//        Collection<Record> response = healthConnectClient.readRecords(new ReadRecordsRequest<>
//                (kotlin.jvm.JvmClassMappingKt.getKotlinClass(StepsRecord.class), TimeRangeFilter.between(startTime, endTime), new HashSet<>(), true, 0, ""));
//
//    }


    public void destroy() {
//        passiveMonitoringClient.clearPassiveListenerCallbackAsync();
    }
}
