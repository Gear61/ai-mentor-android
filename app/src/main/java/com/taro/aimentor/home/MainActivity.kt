package com.taro.aimentor.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.util.UIUtil

class MainActivity : AppCompatActivity(), RestClient.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var conversationManager: ConversationManager
    private lateinit var restClient: RestClient
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversationManager = ConversationManager()
        restClient = RestClient(listener = this)

        conversationAdapter = ConversationAdapter()
        binding.conversationList.adapter = conversationAdapter
        bindComposer()
    }

    private fun bindComposer() {
        binding.sendMessageButton.setOnClickListener {
            UIUtil.hideKeyboard(activity = this)
            val textInput = binding.messageInput.text.toString().trim()
            if (textInput.isBlank()) {
                UIUtil.showLongToast(
                    stringId = R.string.blank_input_error,
                    context = this
                )
                return@setOnClickListener
            }
            conversationManager.onUserMessageSubmitted(textInput = textInput)
            val updatedConversation = conversationManager.getMessages()
            conversationAdapter.submitList(updatedConversation)
            binding.messageInput.setText("")
            restClient.getChatGPTResponse(conversation = updatedConversation)
        }
    }

    override fun onResponseFetched(response: String) {
        conversationManager.onChatGPTResponseReturned(response = response)
        conversationAdapter.submitList(conversationManager.getMessages())
    }

    override fun onResponseFailure() {
        UIUtil.showLongToast(
            stringId = R.string.chatgpt_error,
            context = this
        )
    }
}
