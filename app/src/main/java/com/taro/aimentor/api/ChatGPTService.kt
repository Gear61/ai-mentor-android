package com.taro.aimentor.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatGPTService {

    @GET("chat/completions")
    fun talkToChatGPT(@Query("page") page: Int, @Query("size") size: String): Call<ChatGPTApiResponse>
}
