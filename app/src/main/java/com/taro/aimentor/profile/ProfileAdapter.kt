package com.taro.aimentor.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.taro.aimentor.R
import com.taro.aimentor.profile.ProfileItem.Type.*
import com.taro.aimentor.util.UIUtil

open class ProfileAdapter(
    var listItems: List<ProfileItem>,
    var listener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Listener {

        fun onDisplayNameClicked()

        fun onProfileItemClicked(profileItem: ProfileItem)

        fun setDarkMode(isToggled: Boolean)
    }

    companion object {
        const val USER_INFO_VIEW_TYPE = 0
        const val SECTION_HEADER_VIEW_TYPE = 1
        const val DARK_MODE_TOGGLE_VIEW_TYPE = 2
        const val SETTINGS_ITEM_VIEW_TYPE = 3
    }

    fun onDisplayNameChanged(newName: String) {
        listItems[0].selfUser.displayName = newName
        notifyItemChanged(0)
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position].type) {
            USER_INFO -> USER_INFO_VIEW_TYPE
            SECTION_HEADER -> SECTION_HEADER_VIEW_TYPE
            DARK_MODE_TOGGLE -> DARK_MODE_TOGGLE_VIEW_TYPE
            SETTINGS_ITEM -> SETTINGS_ITEM_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            USER_INFO_VIEW_TYPE -> {
                UserInfoViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.profile_user_info,
                        parent,
                        false
                    )
                )
            }
            SECTION_HEADER_VIEW_TYPE -> {
                SectionHeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.profile_section_header,
                        parent,
                        false
                    )
                )
            }
            DARK_MODE_TOGGLE_VIEW_TYPE -> {
                DarkModeToggleViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.profile_dark_mode_toggle,
                        parent,
                        false
                    )
                )
            }
            SETTINGS_ITEM_VIEW_TYPE -> {
                SettingsItemViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.profile_settings_item,
                        parent,
                        false
                    )
                )
            }
            else -> {
                error("Unsupported home feed item view type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (listItems[position].type) {
            USER_INFO -> (holder as UserInfoViewHolder).bind(position = position)
            SECTION_HEADER -> (holder as SectionHeaderViewHolder).bind(position = position)
            DARK_MODE_TOGGLE -> (holder as DarkModeToggleViewHolder).bind(position = position)
            SETTINGS_ITEM -> (holder as SettingsItemViewHolder).bind(position = position)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class UserInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profilePicture: ImageView = itemView.findViewById(R.id.profile_image)
        private val displayName: TextView = itemView.findViewById(R.id.profile_display_name)
        private val email: TextView = itemView.findViewById(R.id.profile_email)

        fun bind(position: Int) {
            val item = listItems[position]
            profilePicture.load(item.selfUser.photoUrl)

            val userDisplayName = item.selfUser.displayName
            if (userDisplayName.isBlank()) {
                displayName.setText(R.string.click_to_set_name)
                displayName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_footer))
            } else {
                displayName.text = item.selfUser.displayName
                displayName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_title))
            }
            displayName.setOnClickListener {
                listener.onDisplayNameClicked()
            }
            email.text = item.selfUser.email
        }
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.section_header_text)

        fun bind(position: Int) {
            headerText.text = listItems[position].text
        }
    }

    inner class DarkModeToggleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val toggle: SwitchCompat = itemView.findViewById(R.id.dark_mode_toggle)

        fun bind(position: Int) {
            UIUtil.setCheckedImmediately(
                checkableView = toggle,
                checked = listItems[position].isToggled
            )
            itemView.setOnClickListener {
                val newValue = !toggle.isChecked
                toggle.isChecked = newValue
                listener.setDarkMode(isToggled = newValue)
            }
            toggle.setOnClickListener {
                listener.setDarkMode(isToggled = toggle.isChecked)
            }
        }
    }

    inner class SettingsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val settingsText: TextView = itemView.findViewById(R.id.profile_text)
        private val settingsIcon: TextView = itemView.findViewById(R.id.profile_icon)

        fun bind(position: Int) {
            val item = listItems[position]
            settingsText.text = item.text
            settingsIcon.text = item.iconText
            itemView.setOnClickListener {
                listener.onProfileItemClicked(profileItem = item)
            }
        }
    }
}
