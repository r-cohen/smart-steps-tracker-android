package com.r.cohen.smartstepstracker.ui.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.r.cohen.smartstepstracker.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }
    }
}