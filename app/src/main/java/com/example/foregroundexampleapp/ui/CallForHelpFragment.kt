package com.example.foregroundexampleapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foregroundexampleapp.databinding.FragmentCallForHelpBinding

private const val TAG = "Call For Help Fragment"

class CallForHelpFragment : Fragment() {

    private lateinit var binding: FragmentCallForHelpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCallForHelpBinding.inflate(layoutInflater, container, false)
        Log.d(TAG, "Call For Help Fragment View Created")

        return binding.root
    }
}