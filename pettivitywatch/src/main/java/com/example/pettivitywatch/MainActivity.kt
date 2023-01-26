package com.example.pettivitywatch

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.BoxInsetLayout
import com.example.pettivitywatch.communication.Communication
import com.example.pettivitywatch.communication.MessageReceiver
import com.example.pettivitywatch.databinding.ActivityMainBinding
import com.example.pettivitywatch.fragments.DashboardFragment
import com.example.pettivitywatch.fragments.DebugScreenFragment
import com.example.pettivitywatch.models.AmbientListener
import com.example.pettivitywatch.models.User
import com.example.pettivitywatch.models.User.Score


class MainActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensors: Sensors
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private lateinit var background: BoxInsetLayout

    // Fragments
    private val ambientListeners: ArrayList<AmbientListener> = ArrayList()
    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var debugScreenFragment: DebugScreenFragment

    // User data
    private lateinit var lastScore: User.Score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup the communication
        val communication = Communication(this)

        // setup the fragments
        dashboardFragment = DashboardFragment()
        debugScreenFragment = DebugScreenFragment()
        ambientListeners.add(dashboardFragment)
        ambientListeners.add(debugScreenFragment)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, dashboardFragment).commit()

        background = findViewById(R.id.background)

        // Register the sensors and connect to the communication
        sensors = Sensors(this)
        sensors.register(communication, this::sensorUpdate)

        ambientController = AmbientModeSupport.attach(this)

        // Register to receive local broadcasts from the MessageService

        /* ----- Watch ----- */
        // Register to receive local broadcasts from the MessageService
        val messageFilter = IntentFilter(Intent.ACTION_SEND)
        val messageReceiver = MessageReceiver(this::receiveMessage, this::receiveScore)
        LocalBroadcastManager.getInstance(this.applicationContext)
                                .registerReceiver(messageReceiver, messageFilter)
    }

    private fun receiveMessage(message: String) {

    }
    private fun receiveScore(score: Score) {
        dashboardFragment.updateScore(score)
    }

    private fun sensorUpdate(bpm: Float) {
        if (bpm > 0) {
            dashboardFragment.updateHeartRate(bpm)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensors.unregister()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()

    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            // Handle entering ambient mode
            background.setBackgroundColor(resources.getColor(R.color.ambient_background))
            ambientListeners.forEach{it.onEnterAmbient()}
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode
            background.setBackgroundColor(resources.getColor(R.color.background))
            ambientListeners.forEach{it.onExitAmbient()}
        }

        override fun onUpdateAmbient() {
            // Update the content
        }
    }

}