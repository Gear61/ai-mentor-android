package com.taro.aimentor.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.taro.aimentor.models.ChatMessage

class ChatGPTRequestBody {

    @SerializedName("model")
    @Expose
    val model: String = "gpt-3.5-turbo"

    @SerializedName("messages")
    @Expose
    var messages: List<ChatMessage> = listOf()

    @SerializedName("temperature")
    @Expose
    val temperature: Float = 0.5f
}
