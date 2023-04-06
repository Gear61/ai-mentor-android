package com.taro.aimentor.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header(AUTHORIZATION, BEARER_TOKEN_PREFIX + CHATGPT_API_KEY)
            .build()
        return chain.proceed(request)
    }
}
