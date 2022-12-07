package com.r.cohen.smartstepstracker.ui.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.r.cohen.smartstepstracker.databinding.FragmentWeekBinding

class WeekFragment : Fragment() {
    private lateinit var binding: FragmentWeekBinding
    private val viewModel: WeekViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeekBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribeToEvents()
    }

    override fun onStop() {
        super.onStop()
        viewModel.unsubscribeEvents()
    }
}