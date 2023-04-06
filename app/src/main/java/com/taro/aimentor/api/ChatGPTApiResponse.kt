package com.taro.aimentor.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.taro.aimentor.models.ChatMessage

class ChatGPTApiResponse {

    @SerializedName("id")
    @Expose
    val id: String = ""

    @SerializedName("object")
    @Expose
    val responseType: String = ""

    @SerializedName("created")
    @Expose
    val createdTime: Long = 0L

    @SerializedName("model")
    @Expose
    val model: String = ""

    @SerializedName("choices")
    @Expose
    val choices: List<BotChoice> = listOf()

    class BotChoice {

        @SerializedName("message")
        @Expose
        val message: ChatMessage = ChatMessage()

        @SerializedName("finish_reason")
        @Expose
        val finishReason: String = ""

        @SerializedName("index")
        @Expose
        val index: Int = 0
    }
}
