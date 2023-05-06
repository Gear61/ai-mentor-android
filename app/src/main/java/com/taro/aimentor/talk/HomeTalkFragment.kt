package com.taro.aimentor.talk

import android.Manifest
import android.content.pm.PackageManager
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
import com.taro.aimentor.util.PermissionUtil
import com.taro.aimentor.util.UIUtil

class HomeTalkFragment: Fragment(), RestClient.Listener, SpeechToTextManager.Listener {

    companion object {

        fun getInstance(): HomeTalkFragment {
            return HomeTalkFragment()
        }

        private const val RECORD_AUDIO_PERMISSION_CODE = 1
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

        conversationManager = ConversationManager()
        val activity = requireActivity() as MainActivity
        conversationAdapter = ConversationAdapter(listener = activity)
        binding.conversationList.adapter = conversationAdapter

        bindSpeechButton()
    }

    private fun bindSpeechButton() {
        binding.talkingInputButton.setOnClickListener {
            val hasSpeechPermission = PermissionUtil.isPermissionGranted(
                permission = Manifest.permission.RECORD_AUDIO,
                context = it.context
            )
            if (hasSpeechPermission) {
                startSpeechToTextFlow()
            } else {
                PermissionUtil.requestPermission(
                    fragment = this,
                    permission = Manifest.permission.RECORD_AUDIO,
                    requestCode = RECORD_AUDIO_PERMISSION_CODE
                )
            }
        }
    }

    private fun startSpeechToTextFlow() {
        val activity = requireActivity() as MainActivity
        activity.textToSpeechManager.stopSpeaking()
        speechToTextManager.startSpeechToTextFlow()
    }

    override fun onTextSpoken(spokenText: String) {
        conversationManager.onUserMessageSubmitted(textInput = spokenText)
        conversationAdapter.submitList(conversationManager.getMessagesForUi())

        // Clean up the UI
        binding.chatEmptyState.visibility = View.GONE
        binding.conversationList.post {
            binding.conversationList.smoothScrollToPosition(conversationAdapter.itemCount - 1)
        }

        restClient.getChatGPTResponse(
            conversation = conversationManager.getMessagesForApi(context = requireActivity())
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToTextFlow()
            } else {
                UIUtil.showLongToast(
                    R.string.speech_to_text_permission_denied,
                    requireActivity()
                )
            }
        }
    }

    override fun onResponseFetched(response: String) {
        conversationManager.onChatGPTResponseReturned(response = response)
        val activity = requireActivity() as MainActivity
        activity.textToSpeechManager.speak(text = response)

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

    override fun onDestroy() {
        speechToTextManager.cleanUp()
        super.onDestroy()
    }
}
