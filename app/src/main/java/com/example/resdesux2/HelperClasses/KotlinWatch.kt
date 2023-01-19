package com.example.resdesux2.HelperClasses

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.concurrent.timer

class KotlinWatch(context: Context, activity: AppCompatActivity) {
    // ...// Health Connect is available and installed.
    var healthConnectClient: HealthConnectClient? = null
    init {


        if (HealthConnectClient.isAvailable(context)) {
            // Health Connect is available and installed.
            healthConnectClient = HealthConnectClient.getOrCreate(context)

            // build a set of permissions for required data types
            val PERMISSIONS = setOf(
                    HealthPermission.createReadPermission(HeartRateRecord::class),
                    HealthPermission.createWritePermission(HeartRateRecord::class),
                    )

            // Create the permissions launcher.
            val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

            val requestPermissions = activity.registerForActivityResult(requestPermissionActivityContract) { granted ->
                if (granted.containsAll(PERMISSIONS)) {
                    // Permissions successfully granted
                } else {
                    // Lack of required permissions
                }
            }
        } else {
            // ...
            Log.e("Test", "Health connection app not found")
        }
    }

    fun run() {
//        val mainHandler = Handler(Looper.getMainLooper())
//
//        mainHandler.post(object : Runnable {
//            override fun run() {
//                runBlocking {
//                    Log.i("Test", "run: ")
//                    healthConnectClient?.let { readStepsByTimeRange(it, Instant.now().minusSeconds(10), Instant.now().plusSeconds(10)) }
//                }
//
//                mainHandler.postDelayed(this, 1000)
//            }
//        })

        timer(period = 10000) {
            Log.i("Test", "run: ")
            runBlocking {
                healthConnectClient?.let { readStepsByTimeRange(it, Instant.now().minusSeconds(10), Instant.now().plusSeconds(10)) }
            }
        }
    }

    suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient, PERMISSIONS: Set<HealthPermission>, activity: AppCompatActivity) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions(PERMISSIONS)
        if (granted.containsAll(PERMISSIONS)) {
            // Permissions already granted, proceed with inserting or reading data.
        } else {

//            activity.requestPermissions.launch(PERMISSIONS)
//            activity.requestPermissions()
        }
    }

    suspend fun readStepsByTimeRange(healthConnectClient: HealthConnectClient,
            startTime: Instant,endTime: Instant) {
        Log.i("Test", "readStepsByTimeRange: ")
        val response = healthConnectClient.readRecords(
                    ReadRecordsRequest(
                            HeartRateRecord::class,
                            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
            )
        Log.i("Test", "response: " + response + ", records: " + response.records)
        for (heartRecord in response.records) {
            Log.e("Test", "readStepsByTimeRange: $heartRecord")
        }
    }

}