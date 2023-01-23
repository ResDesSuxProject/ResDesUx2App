package com.example.pettivitywatch

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.example.pettivitywatch.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensors: Sensors;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensors = Sensors(this)
        sensors.register(findViewById<TextView>(R.id.test))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
//        sensors.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensors.unregister()
    }
}