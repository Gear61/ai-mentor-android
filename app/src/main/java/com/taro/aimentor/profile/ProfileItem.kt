package com.taro.aimentor.profile

import com.taro.aimentor.models.AIMentorUser

class ProfileItem(
    var type: Type,
    var clickActionType: ClickActionType = ClickActionType.NONE,
    var selfUser: AIMentorUser = AIMentorUser(),
    val text: String = "",
    var iconText: String = "",
    var isToggled: Boolean = false
) {

    enum class Type {
        USER_INFO,
        SECTION_HEADER,
        DARK_MODE_TOGGLE,
        SETTINGS_ITEM
    }

    enum class ClickActionType {
        NONE,
        TOGGLE_DARK_MODE,
        SEND_FEEDBACK,
        RATE_APP,
        SHARE_APP,
        TERMS_AND_CONDITIONS,
        PRIVACY_POLICY,
        LOG_OUT
    }
}
