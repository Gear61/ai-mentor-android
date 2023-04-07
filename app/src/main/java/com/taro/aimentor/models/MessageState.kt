package com.taro.aimentor.models

enum class MessageState {
    // Message has resolved and has text
    COMPLETE,

    // ChatGPT is thinking
    LOADING
}
