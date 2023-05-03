package com.example.foregroundexampleapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foregroundexampleapp.databinding.FragmentManOverBoardBinding

class ManOverBoardFragment : Fragment() {
    private lateinit var binding: FragmentManOverBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentManOverBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}