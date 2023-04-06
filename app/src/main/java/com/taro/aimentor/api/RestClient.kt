package com.taro.aimentor.api

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

    /* fun fetchPictures(page: Int) {
        catService.fetchCatPictures(page, ApiConstants.SMALL).enqueue(object :
            Callback<List<CatPictureUrl>> {
            override fun onResponse(call: Call<List<CatPictureUrl>>, response: Response<List<CatPictureUrl>>) {
                if (response.isSuccessful) {
                    val urls = response.body()
                    if (urls != null) {
                        thumbnailUrls = response.body()
                        maybeReturnResponse()
                    } else {
                        onPictureFetchFail()
                    }
                } else {
                    onPictureFetchFail()
                }
            }

            override fun onFailure(call: Call<List<CatPictureUrl>>, t: Throwable) {
                onPictureFetchFail()
            }
        })
    } */
}
