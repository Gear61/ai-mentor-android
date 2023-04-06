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
        // Need to return a new object, because submitList() early returns on references being the same
        // This means that even if the list has updated content, the contents won't redraw...
        val conversationCopy = mutableListOf<ChatMessage>()
        conversationCopy.addAll(messages)
        return conversationCopy
    }
}
