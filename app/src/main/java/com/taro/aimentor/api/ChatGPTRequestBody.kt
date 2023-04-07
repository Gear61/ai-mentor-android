package com.taro.aimentor.api

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.taro.aimentor.models.ChatMessage

@Keep
class ChatGPTRequestBody(conversation: List<ChatMessage>) {

    @SerializedName("model")
    @Expose
    var model: String = GPT_MODEL_TYPE

    @SerializedName("messages")
    @Expose
    var messages: List<ChatMessage> = listOf()

    @SerializedName("temperature")
    @Expose
    var temperature: Float = MODEL_TEMPERATURE

    init {
        this.messages = conversation
    }
}
