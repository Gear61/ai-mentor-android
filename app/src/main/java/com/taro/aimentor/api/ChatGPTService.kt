package com.taro.aimentor.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGPTService {

    @POST("chat/completions")
    fun talkToChatGPT(@Body requestBody: ChatGPTRequestBody): Call<ChatGPTApiResponse>
}
