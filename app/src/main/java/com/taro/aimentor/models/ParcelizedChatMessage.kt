package com.taro.aimentor.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ParcelizedChatMessage(
    val role: String = "",
    val content: String = ""
) : Parcelable
