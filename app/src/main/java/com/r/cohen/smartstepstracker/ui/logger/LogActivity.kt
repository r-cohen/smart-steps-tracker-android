package com.r.cohen.smartstepstracker.ui.logger

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.r.cohen.smartstepstracker.databinding.ActivityLogBinding
import com.r.cohen.smartstepstracker.logger.Logger

class LogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        binding.layoutOutput.removeAllViews()
        Logger.getLogs().forEach { log -> outputText(log) }
        binding.scrollViewOutput.post {
            binding.scrollViewOutput.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun outputText(text: String) = runOnUiThread {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        binding.layoutOutput.addView(textView)
    }
}