package com.taro.aimentor.profile

import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.taro.aimentor.R
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.theme.ThemeManager
import com.taro.aimentor.theme.ThemeMode
import com.taro.aimentor.util.UIUtil

class SettingsAdapter(
    resources: Resources,
    private val preferencesManager: PreferencesManager,
    private val listener: Listener
) : RecyclerView.Adapter<SettingsAdapter.SettingViewHolder>() {

    private var options: Array<String> = resources.getStringArray(R.array.settings_options)
    private var icons: Array<String> = resources.getStringArray(R.array.settings_icons)

    interface Listener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.settings_list_item,
            parent,
            false)
        return SettingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.loadSetting(position)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    inner class SettingViewHolder(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
        private var icon: TextView = rootView.findViewById(R.id.settings_icon)
        private var option: TextView = rootView.findViewById(R.id.settings_option)
        private var toggle: SwitchCompat = rootView.findViewById(R.id.toggle)

        fun loadSetting(position: Int) {
            icon.text = icons[position]
            option.text = options[position]
            if (position == SettingsFragment.DARK_MODE_POSITION) {
                toggle.visibility = View.VISIBLE
                val isFollowingSystemTheme = preferencesManager.themeMode == ThemeMode.FOLLOW_SYSTEM
                val uiMode = toggle.context.resources.configuration.uiMode
                if (preferencesManager.themeMode == ThemeMode.DARK ||
                    (isFollowingSystemTheme && uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                ) {
                    UIUtil.setCheckedImmediately(toggle, true)
                } else {
                    UIUtil.setCheckedImmediately(toggle, false)
                }
                toggle.setOnClickListener {
                    val themeMode: Int =
                        if (toggle.isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                    preferencesManager.themeMode = themeMode
                    ThemeManager.applyTheme(themeMode)
                }
            } else {
                toggle.visibility = View.GONE
            }
            rootView.setOnClickListener {
                listener.onItemClick(position = position)
            }
        }
    }
}
