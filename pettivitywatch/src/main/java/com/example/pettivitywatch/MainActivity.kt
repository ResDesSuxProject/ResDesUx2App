package com.example.pettivitywatch

import android.app.Activity
import android.os.Bundle
import com.example.pettivitywatch.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensors: Sensors;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensors = Sensors(this)
        sensors.register(findViewById(R.id.test))
    }

    override fun onDestroy() {
        super.onDestroy()
        sensors.unregister()
    }
}