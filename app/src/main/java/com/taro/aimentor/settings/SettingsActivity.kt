package com.taro.aimentor.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.taro.aimentor.R
import com.taro.aimentor.databinding.SettingsBinding
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.theme.ThemeManager
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.UIUtil

class SettingsActivity : AppCompatActivity(), SettingsAdapter.Listener {

    companion object {
        const val SUPPORT_EMAIL = "alex@jointaro.com"
        const val DARK_MODE_POSITION = 1
        const val REPO_URL = "https://github.com/Gear61/ai-mentor-android"
    }

    private lateinit var binding: SettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.line_divider)!!)
        binding.settingsOptions.addItemDecoration(itemDecorator)
        binding.settingsOptions.adapter = SettingsAdapter(
            resources = resources,
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
                val preferencesManager = PreferencesManager.getInstance(this)
                val secondCell: View = binding.settingsOptions.getChildAt(DARK_MODE_POSITION)
                val darkModeToggle = secondCell.findViewById<SwitchCompat>(R.id.toggle)
                darkModeToggle.isChecked = !darkModeToggle.isChecked
                val themeMode: Int = if (darkModeToggle.isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                preferencesManager.themeMode = themeMode
                ThemeManager.applyTheme(themeMode)
                return
            }
            2 -> {
                val shareIntent = ShareCompat.IntentBuilder(this)
                    .setChooserTitle(R.string.share_ai_mentor_with)
                    .setType("text/plain")
                    .setText(getString(R.string.share_app_message))
                    .intent
                if (shareIntent.resolveActivity(packageManager) != null) {
                    startActivity(shareIntent)
                }
                return
            }
            3 -> {
                val packageName = packageName
                val uri = Uri.parse("market://details?id=$packageName")
                intent = Intent(Intent.ACTION_VIEW, uri)
                if (packageManager.queryIntentActivities(intent, 0).size <= 0) {
                    UIUtil.showLongToast(
                        stringId = R.string.play_store_error,
                        context = this
                    )
                    return
                }
            }
            4 -> intent = Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL))
        }
        startActivity(intent)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
