package com.r.cohen.smartstepstracker.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.r.cohen.smartstepstracker.ui.day.TodayFragment
import com.r.cohen.smartstepstracker.ui.month.MonthFragment
import com.r.cohen.smartstepstracker.ui.week.WeekFragment

class MainTabsAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when(position) {
        1 -> WeekFragment()
        2 -> MonthFragment()
        else -> TodayFragment()
    }
}