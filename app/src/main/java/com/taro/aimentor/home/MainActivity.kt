package com.taro.aimentor.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.settings.SettingsActivity
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
            if (conversationManager.isChatGPTThinking()) {
                UIUtil.showLongToast(
                    stringId = R.string.wait_for_response_error,
                    context = this
                )
                return@setOnClickListener
            }

            val textInput = binding.messageInput.text.toString().trim()
            if (textInput.isBlank()) {
                UIUtil.showLongToast(
                    stringId = R.string.blank_input_error,
                    context = this
                )
                return@setOnClickListener
            }
            conversationManager.onUserMessageSubmitted(textInput = textInput)
            conversationAdapter.submitList(conversationManager.getAllMessages())

            // Clean up the UI
            binding.chatEmptyState.visibility = View.GONE
            binding.messageInput.setText("")
            binding.commentComposer.requestFocus()
            binding.conversationList.post {
                binding.conversationList.smoothScrollToPosition(conversationAdapter.itemCount - 1)
            }

            restClient.getChatGPTResponse(conversation = conversationManager.getOnlyCompleteMessages())
        }
    }

    override fun onResponseFetched(response: String) {
        conversationManager.onChatGPTResponseReturned(response = response)
        val updatedConversation = conversationManager.getAllMessages()
        conversationAdapter.submitList(updatedConversation)
        conversationAdapter.notifyItemChanged(conversationAdapter.itemCount - 1)
        binding.conversationList.smoothScrollToPosition(conversationAdapter.itemCount - 1)
    }

    override fun onResponseFailure() {
        UIUtil.showLongToast(
            stringId = R.string.chatgpt_error,
            context = this
        )
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        UIUtil.loadMenuIcon(
            menu = menu!!,
            itemId = R.id.settings,
            icon = MaterialDesignIconic.Icon.gmi_settings,
            context = this
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
