package com.r.cohen.smartstepstracker.ui.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.r.cohen.smartstepstracker.BuildConfig
import com.r.cohen.smartstepstracker.R
import com.r.cohen.smartstepstracker.SmartStepsTrackerApp
import com.r.cohen.smartstepstracker.repo.StepsTrackerRepo
import com.r.cohen.smartstepstracker.store.SmartStepsTrackerPrefs
import com.r.cohen.smartstepstracker.ui.logger.LogActivity

class SettingsFragment: PreferenceFragmentCompat() {
    private var versionPrefClickCount = 0

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey)

        findPreference<Preference>("preference_version")?.let { prefVersion ->
            prefVersion.summary = BuildConfig.VERSION_NAME
            //prefVersion.isSelectable = false
            prefVersion.setOnPreferenceClickListener { _ ->
                versionPrefClickCount++
                if (versionPrefClickCount >= 10) {
                    versionPrefClickCount = 0
                    requireContext().startActivity(Intent(requireContext(), LogActivity::class.java))
                }
                return@setOnPreferenceClickListener true
            }
        }

        findPreference<Preference>("preference_clear_data")?.setOnPreferenceClickListener { _ ->
            with (MaterialAlertDialogBuilder(requireContext())) {
                setTitle(R.string.clear_history_summary)
                setMessage(R.string.confirm_delete_history)
                setPositiveButton(R.string.delete_all) { dialog, _ ->
                    dialog.dismiss()

                    StepsTrackerRepo.deleteAll { result ->
                        val toastMsg =
                            if (result) R.string.history_data_deleted
                            else R.string.something_went_wrong
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                create().apply { show() }
            }
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("preference_credits")?.setOnPreferenceClickListener { _ ->
            with (MaterialAlertDialogBuilder(requireContext())) {
                setTitle(R.string.credits_summary)
                setMessage(R.string.about_content)
                setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                setNeutralButton(R.string.send_feedback) { dialog, _ ->
                    dialog.dismiss()
                    with (
                        Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("mailto:raphael.cohen@gmail.com"))
                    ) {
                        startActivity(Intent.createChooser(this, getString(R.string.send_feedback)))
                    }
                }
                create().apply { show() }
            }
            return@setOnPreferenceClickListener true
        }

        val selectedThemeValue = SmartStepsTrackerPrefs.getSelectedThemeValue()
        val themePref = findPreference<ListPreference>("preference_theme")
        themePref?.setDefaultValue(selectedThemeValue)
        themePref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue != selectedThemeValue) {
                SmartStepsTrackerPrefs.setSelectedThemeValue(newValue as String)
                SmartStepsTrackerApp.instance.setAppTheme(newValue)
            }
            return@setOnPreferenceChangeListener true
        }
    }
}