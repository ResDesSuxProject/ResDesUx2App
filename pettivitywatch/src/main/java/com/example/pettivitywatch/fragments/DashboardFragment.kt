package com.example.pettivitywatch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.pettivitywatch.R
import com.example.pettivitywatch.models.AmbientListener

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment(), AmbientListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    fun updateHeartRate(bpm: Float) {
        view?.findViewById<TextView>(R.id.dashboard_subTitle)?.text = (getString(R.string.your_heart_rate, bpm))
    }

    override fun onEnterAmbient() {
        view?.findViewById<CardView>(R.id.dashboard_animated_dog)?.visibility = CardView.INVISIBLE
        view?.findViewById<LinearLayout>(R.id.dashboard_title_container)?.background = null
        view?.findViewById<ImageView>(R.id.dashboard_static_dog)?.visibility = ImageView.VISIBLE
    }

    override fun onExitAmbient() {
        view?.findViewById<CardView>(R.id.dashboard_animated_dog)?.visibility = CardView.VISIBLE
        view?.findViewById<LinearLayout>(R.id.dashboard_title_container)?.setBackgroundColor(resources.getColor(R.color.txt_background))
        view?.findViewById<ImageView>(R.id.dashboard_static_dog)?.visibility = ImageView.INVISIBLE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment DashboardFragment.
         */
        @JvmStatic
        fun newInstance() =
            DashboardFragment()
    }
}