package com.example.pettivitywatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.pettivitywatch.R
import com.example.pettivitywatch.models.ServerInteraction

class ConnectServerFragment : Fragment() {
    private var serverInteraction: ServerInteraction? = null
    private var serverInput: EditText? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_connect_server, container, false)

        serverInput = view.findViewById(R.id.server_ip);
        if (serverInteraction != null) serverInput?.setText(serverInteraction?.serverIP)

        return view
    }

    fun setServerHandler(serverInteraction: ServerInteraction) {
        this.serverInteraction = serverInteraction;
        serverInput?.setText(serverInteraction.serverIP)
        serverInteraction.setConnectionFailedListener(this::connectionFailed)
    }

    private fun connectionFailed(_boolean: Boolean) {
        val updateIPBtn: Button? = view?.findViewById(R.id.updateIPBtn);
        updateIPBtn?.isEnabled = true
        updateIPBtn?.setBackgroundColor(resources.getColor(R.color.primary))
    }
}