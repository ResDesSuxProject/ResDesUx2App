package com.example.pettivitywatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pettivitywatch.R
import com.example.pettivitywatch.models.ServerInteraction

class LoginFragment : Fragment() {
    private var serverInteraction: ServerInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    fun setServerHandler(serverInteraction: ServerInteraction) {
        this.serverInteraction = serverInteraction;
        serverInteraction
    }
}