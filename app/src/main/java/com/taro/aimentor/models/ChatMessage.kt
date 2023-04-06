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

    fun getType(): MessageType {
        return when (role) {
            "user" -> MessageType.USER
            "assistant" -> MessageType.ASSISTANT
            "system" -> MessageType.SYSTEM
            else -> error("Unexpected message type - Role doesn't match!")
        }
    }
}
