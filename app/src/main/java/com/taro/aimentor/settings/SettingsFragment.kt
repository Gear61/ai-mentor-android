package com.taro.aimentor.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.taro.aimentor.R
import com.taro.aimentor.databinding.SettingsBinding
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.theme.ThemeManager
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.UIUtil

class SettingsFragment: Fragment(), SettingsAdapter.Listener {

    companion object {

        fun getInstance(): SettingsFragment {
            return SettingsFragment()
        }

        const val SUPPORT_EMAIL = "alex@jointaro.com"
        const val DARK_MODE_POSITION = 1
        const val REPO_URL = "https://github.com/Gear61/ai-mentor-android"
    }

    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemDecorator = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.line_divider)!!)
        binding.settingsOptions.addItemDecoration(itemDecorator)
        binding.settingsOptions.adapter = SettingsAdapter(
            resources = resources,
            preferencesManager = PreferencesManager.getInstance(view.context),
            listener = this
        )
    }

    override fun onItemClick(position: Int) {
        var intent: Intent? = null
        when (position) {
            0 -> {
                val uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(getString(R.string.feedback_subject))
                val mailUri = Uri.parse(uriText)
                val sendIntent = Intent(Intent.ACTION_SENDTO, mailUri)
                startActivity(Intent.createChooser(sendIntent, getString(R.string.send_email)))
                return
            }
            1-> {
                val preferencesManager = PreferencesManager.getInstance(requireActivity())
                val secondCell: View = binding.settingsOptions.getChildAt(DARK_MODE_POSITION)
                val darkModeToggle = secondCell.findViewById<SwitchCompat>(R.id.toggle)
                darkModeToggle.isChecked = !darkModeToggle.isChecked
                val themeMode: Int = if (darkModeToggle.isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                preferencesManager.themeMode = themeMode
                ThemeManager.applyTheme(themeMode)
                return
            }
            2 -> {
                val shareIntent = ShareCompat.IntentBuilder(requireActivity())
                    .setChooserTitle(R.string.share_ai_mentor_with)
                    .setType("text/plain")
                    .setText(getString(R.string.share_app_message))
                    .intent
                if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(shareIntent)
                }
                return
            }
            3 -> {
                val packageName = requireActivity().packageName
                val uri = Uri.parse("market://details?id=$packageName")
                intent = Intent(Intent.ACTION_VIEW, uri)
                if (requireActivity().packageManager.queryIntentActivities(intent, 0).size <= 0) {
                    UIUtil.showLongToast(
                        stringId = R.string.play_store_error,
                        context = requireActivity()
                    )
                    return
                }
            }
            4 -> intent = Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL))
        }
        startActivity(intent)
    }
}
