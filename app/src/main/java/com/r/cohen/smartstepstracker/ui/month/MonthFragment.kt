package com.r.cohen.smartstepstracker.ui.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.r.cohen.smartstepstracker.databinding.FragmentMonthBinding

class MonthFragment : Fragment() {
    private lateinit var binding: FragmentMonthBinding
    private val viewModel: MonthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthBinding.inflate(inflater)
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