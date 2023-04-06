package com.taro.aimentor.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChatMessage {

    @SerializedName("role")
    @Expose
    val role: String = ""

    @SerializedName("content")
    @Expose
    val content: String = ""
}
