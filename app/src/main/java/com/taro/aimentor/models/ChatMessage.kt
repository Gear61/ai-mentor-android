package com.taro.aimentor.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChatMessage {

    @SerializedName("role")
    @Expose
    var role: String = ""

    @SerializedName("content")
    @Expose
    var content: String = ""
}
