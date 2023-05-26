package com.taro.aimentor.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import com.taro.aimentor.common.DISPLAY_NAME_KEY
import com.taro.aimentor.common.EMAIL_KEY
import com.taro.aimentor.common.PHOTO_URL_KEY
import com.taro.aimentor.common.USER_ID_KEY
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AIMentorUser(
    @PropertyName(USER_ID_KEY) var userId: String = "",
    @PropertyName(DISPLAY_NAME_KEY) var displayName: String = "",
    @PropertyName(EMAIL_KEY) var email: String = "",
    @PropertyName(PHOTO_URL_KEY) var photoUrl: String = ""
) : Parcelable
