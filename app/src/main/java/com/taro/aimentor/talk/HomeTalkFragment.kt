package com.taro.aimentor.talk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.databinding.HomeTalkBinding
import com.taro.aimentor.home.MainActivity
import com.taro.aimentor.speech.SpeechToTextManager
import com.taro.aimentor.util.UIUtil

class HomeTalkFragment: Fragment(), RestClient.Listener, SpeechToTextManager.Listener {

    companion object {

        fun getInstance(): HomeTalkFragment {
            return HomeTalkFragment()
        }

        private const val CONVERSATION_MESSAGES = "conversation_messages"
    }

    private var _binding: HomeTalkBinding? = null
    private val binding get() = _binding!!

    private lateinit var speechToTextManager: SpeechToTextManager
    private lateinit var conversationManager: ConversationManager
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var restClient: RestClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speechToTextManager = SpeechToTextManager(
            activity = requireActivity(),
            listener = this
        )
        restClient = RestClient(listener = this)

        val activity = requireActivity() as MainActivity
        conversationAdapter = ConversationAdapter(listener = activity)

        binding.talkingInputButton.setOnClickListener {
            speechToTextManager.startSpeechToTextFlow()
        }
    }

    override fun onTextSpoken(spokenText: String?) {

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
}
