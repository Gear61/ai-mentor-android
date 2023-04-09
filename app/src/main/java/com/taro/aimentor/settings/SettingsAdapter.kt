package com.taro.aimentor.settings

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taro.aimentor.R

class SettingsAdapter(
    resources: Resources,
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

        fun loadSetting(position: Int) {
            icon.text = icons[position]
            option.text = options[position]
            rootView.setOnClickListener {
                listener.onItemClick(position = position)
            }
        }
    }
}
