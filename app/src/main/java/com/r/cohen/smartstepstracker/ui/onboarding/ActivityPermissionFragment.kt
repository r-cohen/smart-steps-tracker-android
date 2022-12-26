package com.r.cohen.smartstepstracker.ui.onboarding

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.r.cohen.smartstepstracker.databinding.FragmentActivityPermissionBinding

class ActivityPermissionFragment(private val navigator: OnboardingNavigator): Fragment() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val powerManager = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)
            if (!isIgnoringBatteryOptimizations) {
                navigator.nextPage()
            } else {
                navigator.finishOnboarding()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentActivityPermissionBinding.inflate(inflater)

        binding.permissionButton.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        return binding.root
    }
}