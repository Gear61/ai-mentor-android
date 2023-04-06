package com.taro.aimentor.conversation

import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.models.MessageType

/**
 * Manages the data model behind the conversation.
 **/
class ConversationManager {

    private val messages: MutableList<ChatMessage> = mutableListOf()

    fun onUserMessageSubmitted(textInput: String) {
        messages.add(
            ChatMessage(
                type = MessageType.USER,
                content = textInput
            )
        )
    }

    fun onChatGPTResponseReturned(response: String) {
        messages.add(
            ChatMessage(
                type = MessageType.ASSISTANT,
                content = response
            )
        )
    }

    fun getMessages(): List<ChatMessage> {
        return messages
    }
}
