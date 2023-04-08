package com.taro.aimentor.conversation

import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.models.MessageState
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
        messages.add(
            ChatMessage(type = MessageType.ASSISTANT)
        )
    }

    fun onChatGPTResponseReturned(response: String) {
        messages.last().onMessageComplete(finalContent = response)
    }

    // For passing to the API
    fun getOnlyCompleteMessages(): List<ChatMessage> {
        val conversationCopy = mutableListOf<ChatMessage>()
        conversationCopy.addAll(messages)
        return conversationCopy.filter { it.getState() == MessageState.COMPLETE }
    }

    // For the UI
    fun getAllMessages(): List<ChatMessage> {
        val conversationCopy = mutableListOf<ChatMessage>()
        conversationCopy.addAll(messages)
        return conversationCopy
    }

    fun isChatGPTThinking(): Boolean {
        return messages.isNotEmpty() && messages.last().getState() == MessageState.LOADING
    }
}
