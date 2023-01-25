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
import com.example.pettivitywatch.server.ServerHandler


class MainActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensors: Sensors;
    private lateinit var ambientController: AmbientModeSupport.AmbientController
    private lateinit var testTextView: TextView
    private lateinit var background: BoxInsetLayout

    private lateinit var serverHandler: ServerHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val comm = Communication(this)

        // initialize server:
//        serverHandler = ServerHandler(this)

        // setup server connect fragment
//        val connectServerFragment = ConnectServerFragment();
//        connectServerFragment.setServerHandler(serverHandler);

        // setup login fragment
        val loginFragment = LoginFragment()
//        loginFragment.setServerHandler(serverHandler)

//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, connectServerFragment).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, loginFragment).commit()

        testTextView = findViewById(R.id.test)
        background = findViewById(R.id.background)

        sensors = Sensors(this)
        sensors.register(testTextView)

        ambientController = AmbientModeSupport.attach(this)

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