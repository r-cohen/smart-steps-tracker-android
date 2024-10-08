package com.r.cohen.smartstepstracker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.r.cohen.smartstepstracker.R
import com.r.cohen.smartstepstracker.databinding.ActivityMainBinding
import com.r.cohen.smartstepstracker.stepscounter.StepCountSchedulerService
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs
import com.r.cohen.smartstepstracker.ui.onboarding.PermissionsActivity
import com.r.cohen.smartstepstracker.ui.preferences.PreferencesActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var scheduled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)

        viewModel.settingsClickEvent.observe(this) {
            it?.handle()?.let {
                startActivity(Intent(this, PreferencesActivity::class.java))
            }
        }

        binding.tabsPager.adapter = MainTabsAdapter(this)
        TabLayoutMediator(binding.tabs, binding.tabsPager) { tab, position ->
            tab.text = when(position) {
                1 -> getString(R.string.this_week)
                2 -> getString(R.string.last_30_days)
                else -> getString(R.string.today)
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        var permissionsMissing = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                permissionsMissing = true
            }
        }
        if (permissionsMissing || !SmartStepsTrackerPrefs.isOnboardingPassed()) {
            startActivity(Intent(this, PermissionsActivity::class.java))
            return
        }

        registerAndSchedule()
    }

    private fun registerAndSchedule() {
        if (!scheduled) {
            scheduled = true
            StepCountSchedulerService.schedule(this)
        }
    }
}