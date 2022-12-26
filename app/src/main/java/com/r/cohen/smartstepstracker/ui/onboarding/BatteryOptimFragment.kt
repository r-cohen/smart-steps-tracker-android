package com.r.cohen.smartstepstracker.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.r.cohen.smartstepstracker.databinding.FragmentBatteryOptimBinding

class BatteryOptimFragment(private val navigator: OnboardingNavigator): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBatteryOptimBinding.inflate(inflater)
        binding.permissionButton.setOnClickListener {
            navigator.finishOnboarding()

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireContext().packageName, null)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            requireContext().startActivity(intent)
        }
        binding.skipButton.setOnClickListener {
            navigator.finishOnboarding()
        }
        return binding.root
    }
}