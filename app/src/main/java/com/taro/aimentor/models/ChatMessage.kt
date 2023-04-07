package com.taro.aimentor.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.taro.aimentor.api.ASSISTANT_ROLE
import com.taro.aimentor.api.SYSTEM_ROLE
import com.taro.aimentor.api.USER_ROLE

@Keep
class ChatMessage(
    type: MessageType,
    content: String = ""
) {

    @SerializedName("role")
    @Expose
    var role: String = ""

    @SerializedName("content")
    @Expose
    var content: String = ""

    init {
        role = when (type) {
            MessageType.USER -> USER_ROLE
            MessageType.ASSISTANT -> ASSISTANT_ROLE
            MessageType.SYSTEM -> SYSTEM_ROLE
        }
        this.content = content
    }

    fun getType(): MessageType {
        return when (role) {
            USER_ROLE -> MessageType.USER
            ASSISTANT_ROLE -> MessageType.ASSISTANT
            SYSTEM_ROLE -> MessageType.SYSTEM
            else -> error("Unexpected message type - Role doesn't match!")
        }
    }

    fun getState(): MessageState {
        return if (content.isBlank()) MessageState.LOADING else MessageState.COMPLETE
    }

    fun onMessageComplete(finalContent: String) {
        content = finalContent
    }
}
