package com.taro.aimentor.models

enum class MessageType {
    // From the app user
    USER,

    // Response from ChatGPT
    ASSISTANT,

    // Hidden pretext that is sent to ChatGPT at the beginning of the conversation
    // This info is used to calibrate the assistant (e.g. "You are a career coach for software engineers.")
    SYSTEM
}
