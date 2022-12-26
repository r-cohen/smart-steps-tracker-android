package com.r.cohen.smartstepstracker.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PermissionsPagerAdapter(private val activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        1 -> BatteryOptimFragment(activity as OnboardingNavigator)
        else -> ActivityPermissionFragment(activity as OnboardingNavigator)
    }
}