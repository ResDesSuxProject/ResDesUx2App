package com.example.pettivitywatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pettivitywatch.R
import com.example.pettivitywatch.models.AmbientListener

class DebugScreenFragment : Fragment(), AmbientListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug_screen, container, false)
    }

    override fun onEnterAmbient() {

    }

    override fun onExitAmbient() {
        
    }
}