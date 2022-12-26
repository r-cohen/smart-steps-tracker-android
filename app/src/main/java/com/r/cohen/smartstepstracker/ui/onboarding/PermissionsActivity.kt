package com.r.cohen.smartstepstracker.ui.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.r.cohen.smartstepstracker.databinding.ActivityPermissionsBinding
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs

class PermissionsActivity : OnboardingNavigator, AppCompatActivity() {
    private lateinit var binding: ActivityPermissionsBinding
    private lateinit var pagerAdapter: PermissionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pagerAdapter = PermissionsPagerAdapter(this)
        binding.pagerSteps.isUserInputEnabled = false
        binding.pagerSteps.adapter = pagerAdapter

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            nextPage()
            return
        }

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            nextPage()
            return
        }
    }

    override fun nextPage() {
        if (binding.pagerSteps.currentItem < pagerAdapter.itemCount - 1) {
            binding.pagerSteps.currentItem++
        }
    }

    override fun finishOnboarding() {
        SmartStepsTrackerPrefs.setOnboardingPassed()
        finish()
    }
}