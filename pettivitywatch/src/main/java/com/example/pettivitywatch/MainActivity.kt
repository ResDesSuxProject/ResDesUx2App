package com.example.pettivitywatch

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.BoxInsetLayout
import com.example.pettivitywatch.communication.Communication
import com.example.pettivitywatch.databinding.ActivityMainBinding
import com.example.pettivitywatch.fragments.LoginFragment


class MainActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensors: Sensors;
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private lateinit var testTextView: TextView
    private lateinit var background: BoxInsetLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup the communication
        val communication = Communication(this)

        // setup login fragment
        val loginFragment = LoginFragment()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, loginFragment).commit()

        testTextView = findViewById(R.id.test)
        background = findViewById(R.id.background)

        // Register the sensors and connect to the communication
        sensors = Sensors(this)
        sensors.register(communication, this::sensorUpdate)

        ambientController = AmbientModeSupport.attach(this)
    }

    private fun sensorUpdate(BPM: Float) {
        testTextView.text = "Heart rate: $BPM"
    }

    override fun onDestroy() {
        super.onDestroy()
        sensors.unregister()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback();

    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            // Handle entering ambient mode
            testTextView.setTextColor(Color.GREEN)
            background.setBackgroundColor(resources.getColor(R.color.ambient_background))
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode
            testTextView.setTextColor(Color.WHITE)
            background.setBackgroundColor(resources.getColor(R.color.background))
        }

        override fun onUpdateAmbient() {
            // Update the content
        }
    }

}