package com.example.foregroundexampleapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foregroundexampleapp.MainActivity
import com.example.foregroundexampleapp.R
import com.example.foregroundexampleapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentHomeBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        mainActivity = activity as MainActivity

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBinding.apply {
           compassButton.setOnClickListener {
               mainActivity.replaceFragment(CompassFragment(), "Compass", R.id.compass_fragment, mainActivity.binding.navView)
           }
            marineTipsButton.setOnClickListener {
                mainActivity.replaceFragment(MarineTipsFragment(), "Marine Tips", R.id.marine_tips_fragment, mainActivity.binding.navView)
            }
            weatherButton.setOnClickListener {
                mainActivity.replaceFragment(WeatherFragment(), "Weather", R.id.weather_fragment, mainActivity.binding.navView)
            }
            manOverBoardButton.setOnClickListener {
                mainActivity.replaceFragment(ManOverBoardFragment(), "Man Overboard", R.id.man_over_board_fragment, mainActivity.binding.navView)
            }
        }
    }
}