package com.taro.aimentor.api

import com.taro.aimentor.models.ChatMessage
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient(private var listener: Listener) {

    private var chatGPTService: ChatGPTService

    interface Listener {
        fun onResponseFetched(response: String)

        fun onResponseFailure()
    }

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OPEN_AI_API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        chatGPTService = retrofit.create(ChatGPTService::class.java)
    }

    fun getChatGPTResponse() {
        val requestBody = ChatGPTRequestBody()
        val initialMessage = ChatMessage()
        initialMessage.role = "user"
        initialMessage.content = "What's the capital of France?"
        val messageList = listOf(initialMessage)
        requestBody.messages = messageList

        chatGPTService.talkToChatGPT(requestBody).enqueue(object : Callback<ChatGPTApiResponse> {
            override fun onResponse(call: Call<ChatGPTApiResponse>, response: Response<ChatGPTApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        listener.onResponseFetched(response = apiResponse.getText())
                    } else {
                        listener.onResponseFailure()
                    }
                } else {
                    listener.onResponseFailure()
                }
            }

            override fun onFailure(call: Call<ChatGPTApiResponse>, t: Throwable) {
                listener.onResponseFailure()
            }
        })
    }
}