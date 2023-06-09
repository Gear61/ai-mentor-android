package com.taro.aimentor.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.databinding.HomeChatBinding
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.models.ParcelizedChatMessage
import com.taro.aimentor.util.UIUtil

class HomeChatFragment: Fragment(), RestClient.Listener {

    companion object {

        fun getInstance(): HomeChatFragment {
            return HomeChatFragment()
        }

        private const val CONVERSATION_MESSAGES = "conversation_messages"
    }

    private var _binding: HomeChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var conversationManager: ConversationManager
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var restClient: RestClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restClient = RestClient(listener = this)
        val activity = requireActivity() as MainActivity
        conversationAdapter = ConversationAdapter(listener = activity)

        binding.conversationList.adapter = conversationAdapter
        bindComposer()

        conversationManager = ConversationManager()
        if (savedInstanceState != null) {
            val messageList = savedInstanceState
                .getParcelableArrayList<ParcelizedChatMessage>(CONVERSATION_MESSAGES) as ArrayList<ParcelizedChatMessage>
            if (messageList.isEmpty()) {
                return
            }
            binding.chatEmptyState.visibility = View.GONE
            conversationManager.restoreConversation(messageList = messageList)
            conversationAdapter.submitList(conversationManager.getMessagesForUi())
        }

        val rootView = activity.findViewById<View>(android.R.id.content).rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val keyboardVisible = WindowInsetsCompat
                .toWindowInsetsCompat(rootView.rootWindowInsets)
                .isVisible(WindowInsetsCompat.Type.ime())

            // When the keyboard is open, scroll the conversation to the bottom
            // It looks like you don't get punished for over-scrolling, so using 9001 for the memes
            if (keyboardVisible) {
                binding.conversationList.post {
                    binding.conversationList.smoothScrollBy(0, 9001)
                }
            }
        }
    }

    private fun bindComposer() {
        binding.sendMessageButton.setOnClickListener {
            UIUtil.hideKeyboard(activity = requireActivity())
            if (conversationManager.isChatGPTThinking()) {
                UIUtil.showLongToast(
                    stringId = R.string.wait_for_response_error,
                    context = requireContext()
                )
                return@setOnClickListener
            }

            val textInput = binding.messageInput.text.toString().trim()
            if (textInput.isBlank()) {
                UIUtil.showLongToast(
                    stringId = R.string.blank_input_error,
                    context = requireActivity()
                )
                return@setOnClickListener
            }
            conversationManager.onUserMessageSubmitted(textInput = textInput)
            conversationAdapter.submitList(conversationManager.getMessagesForUi())

            // Clean up the UI
            binding.chatEmptyState.visibility = View.GONE
            binding.messageInput.setText("")
            binding.commentComposer.requestFocus()
            binding.conversationList.post {
                binding.conversationList.smoothScrollToPosition(conversationAdapter.itemCount - 1)
            }

            restClient.getChatGPTResponse(
                conversation = conversationManager.getMessagesForApi(context = requireActivity())
            )
        }
    }

    override fun onResponseFetched(response: String) {
        conversationManager.onChatGPTResponseReturned(response = response)
        val updatedConversation = conversationManager.getMessagesForUi()
        conversationAdapter.submitList(updatedConversation)
        conversationAdapter.notifyItemChanged(conversationAdapter.itemCount - 1)
    }

    override fun onResponseFailure() {
        UIUtil.showLongToast(
            stringId = R.string.chatgpt_error,
            context = requireActivity()
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(
            CONVERSATION_MESSAGES,
            conversationManager.getParcelizedMessageList()
        )
    }

    override fun onDestroy() {
        restClient.cleanUp(activity = requireActivity())
        super.onDestroy()
    }
}
