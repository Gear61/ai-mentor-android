package com.taro.aimentor.conversation

import android.content.Context
import com.taro.aimentor.R
import com.taro.aimentor.api.ASSISTANT_ROLE
import com.taro.aimentor.api.SYSTEM_ROLE
import com.taro.aimentor.api.USER_ROLE
import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.models.MessageState
import com.taro.aimentor.models.MessageType
import com.taro.aimentor.models.ParcelizedChatMessage
import com.taro.aimentor.persistence.PreferencesManager

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

    fun restoreConversation(messageList: List<ParcelizedChatMessage>) {
        for (message in messageList) {
            val messageType = when (message.role) {
                USER_ROLE -> MessageType.USER
                ASSISTANT_ROLE -> MessageType.ASSISTANT
                SYSTEM_ROLE -> MessageType.SYSTEM
                else -> error("Unexpected message type - Role doesn't match!")
            }
            messages.add(
                ChatMessage(
                    type = messageType,
                    content = message.content
                )
            )
        }
    }

    fun onChatGPTResponseReturned(response: String) {
        messages.last().onMessageComplete(finalContent = response)
    }

    // Excludes in-progress messages
    fun getMessagesForApi(context: Context): List<ChatMessage> {
        val conversationCopy = mutableListOf<ChatMessage>()

        val systemMessage = ChatMessage(type = MessageType.SYSTEM)
        val preferencesManager = PreferencesManager.getInstance(context)
        var userOccupation = ""
        userOccupation = if (preferencesManager.occupation == context.getString(R.string.student)) {
            context.getString(R.string.student_component, preferencesManager.fieldOfStudy)
        } else {
            val yoe = preferencesManager.yearsOfExperience
            val yoeText = if (yoe == 1) context.getString(R.string.one_year) else context.getString(R.string.x_years, yoe)
            context.getString(
                R.string.worker_component,
                preferencesManager.occupation,
                yoeText
            )
        }
        var systemPromptText = context.getString(R.string.initial_ai_priming_message, userOccupation)
        if (preferencesManager.isInterviewing) {
            systemPromptText = systemPromptText + " " + context.getString(R.string.job_searching_addendum)
        }
        systemPromptText += context.getString(R.string.focusing_message_for_ai)
        systemMessage.content = systemPromptText

        conversationCopy.add(systemMessage)
        conversationCopy.addAll(messages)
        return conversationCopy.filter { it.getState() == MessageState.COMPLETE }
    }

    // Includes in-progress messages while excluding the system prompt message
    fun getMessagesForUi(): List<ChatMessage> {
        val conversationCopy = mutableListOf<ChatMessage>()
        conversationCopy.addAll(messages)
        return conversationCopy.filter { it.getType() == MessageType.USER || it.getType() == MessageType.ASSISTANT }
    }

    fun getParcelizedMessageList(): ArrayList<ParcelizedChatMessage> {
        val messageParcels = ArrayList<ParcelizedChatMessage>()
        for (message in messages) {
            messageParcels.add(message.toParcelizedMessage())
        }
        return messageParcels
    }

    fun isChatGPTThinking(): Boolean {
        return messages.isNotEmpty() && messages.last().getState() == MessageState.LOADING
    }
}
