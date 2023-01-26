package com.example.pettivitywatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.pettivitywatch.R
import com.example.pettivitywatch.models.AmbientListener
import com.example.pettivitywatch.models.DebugBPMHandler

class DebugScreenFragment : Fragment(), DebugBPMHandler, AmbientListener, OnSeekBarChangeListener {
    private lateinit var fakeBPMText: TextView
    private lateinit var fakeBPMSwitch: SwitchCompat
    private lateinit var fakeBPMSeekbar: SeekBar
    private var isFakeBPMEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_debug_screen, container, false)
        fakeBPMText = view.findViewById(R.id.debug_Fake_BPM_text)
        fakeBPMSwitch = view.findViewById(R.id.debug_switch_fake_BPM)
        fakeBPMSeekbar = view.findViewById(R.id.debug_fake_bpm_seekBar)

        fakeBPMText.text = getString(R.string.debug_fake_bpm, fakeBPMSeekbar.progress)

        fakeBPMSeekbar.setOnSeekBarChangeListener(this)
        fakeBPMSwitch.setOnClickListener(this::fakeBPMSwitchCLicked)
        return view
    }

    override fun onProgressChanged(seekbar: SeekBar?, p1: Int, p2: Boolean) {
        fakeBPMText.text = getString(R.string.debug_fake_bpm, seekbar?.progress)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    fun fakeBPMSwitchCLicked(view: View) {
        synchronized(this) {
            isFakeBPMEnabled = fakeBPMSwitch.isChecked
        }
    }

    override fun onEnterAmbient() {

    }

    override fun onExitAmbient() {

    }

    override fun isEnabled(): Boolean {
        synchronized(this){
            return isFakeBPMEnabled
        }
    }

    override fun getBPM(): Int {
        synchronized(this) {
            return fakeBPMSeekbar.progress
        }
    }
}