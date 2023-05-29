package com.taro.aimentor.profile

import android.content.Context
import android.content.res.Configuration
import com.taro.aimentor.BuildConfig
import com.taro.aimentor.R
import com.taro.aimentor.models.AIMentorUser
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.profile.ProfileItem.Type.*
import com.taro.aimentor.profile.ProfileItem.ClickActionType.*
import com.taro.aimentor.theme.ThemeMode

object ProfileDataModel {

    fun generateItems(
        preferencesManager: PreferencesManager,
        context: Context
    ): List<ProfileItem> {
        val profileItems = mutableListOf<ProfileItem>()

        // User info
        profileItems.add(
            ProfileItem(
                type = USER_INFO,
                selfUser = AIMentorUser(
                    displayName = preferencesManager.userDisplayName,
                    email = preferencesManager.userEmail,
                    photoUrl = preferencesManager.userPhotoUrl
                )
            )
        )

        // Support
        profileItems.add(
            ProfileItem(
                type = SECTION_HEADER,
                text = context.getString(R.string.support)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = SEND_FEEDBACK,
                text = context.getString(R.string.send_feedback),
                iconText = context.getString(R.string.email_icon)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = RATE_APP,
                text = context.getString(R.string.rate_this_app),
                iconText = context.getString(R.string.like_icon)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = SHARE_APP,
                text = context.getString(R.string.share_this_app),
                iconText = context.getString(R.string.share_icon)
            )
        )

        // Settings
        profileItems.add(
            ProfileItem(
                type = SECTION_HEADER,
                text = context.getString(R.string.settings)
            )
        )
        val darkModeFromSystem = preferencesManager.themeMode == ThemeMode.FOLLOW_SYSTEM &&
                context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val isDarkModeToggled =  preferencesManager.themeMode == ThemeMode.DARK || darkModeFromSystem
        profileItems.add(
            ProfileItem(
                type = DARK_MODE_TOGGLE,
                clickActionType = TOGGLE_DARK_MODE,
                isToggled = isDarkModeToggled
            )
        )

        // Legal
        profileItems.add(
            ProfileItem(
                type = SECTION_HEADER,
                text = context.getString(R.string.legal)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = TERMS_AND_CONDITIONS,
                text = context.getString(R.string.terms_and_conditions_settings),
                iconText = context.getString(R.string.document_icon)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = PRIVACY_POLICY,
                text = context.getString(R.string.privacy_policy_settings),
                iconText = context.getString(R.string.document_icon)
            )
        )

        // Other
        profileItems.add(
            ProfileItem(
                type = SECTION_HEADER,
                text = context.getString(R.string.other)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                text = context.getString(R.string.app_version_text, BuildConfig.VERSION_NAME),
                iconText = context.getString(R.string.info_icon)
            )
        )
        profileItems.add(
            ProfileItem(
                type = SETTINGS_ITEM,
                clickActionType = LOG_OUT,
                text = context.getString(R.string.log_out),
                iconText = context.getString(R.string.log_out_icon)
            )
        )

        return profileItems
    }
}
