package com.taro.aimentor.api

const val OPEN_AI_API_BASE_URL = "https://api.openai.com/v1/"
const val AUTHORIZATION = "Authorization"
const val BEARER_TOKEN_PREFIX = "Bearer "

const val GPT_MODEL_TYPE = "gpt-3.5-turbo"

// 0 is the most consistent, 1.0 is the most random and potentially creative
const val MODEL_TEMPERATURE = 0.7f

const val USER_ROLE = "user"
const val ASSISTANT_ROLE = "assistant"
const val SYSTEM_ROLE = "system"
