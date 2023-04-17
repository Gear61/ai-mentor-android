package com.taro.aimentor.home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.view.WindowInsetsCompat
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.taro.aimentor.R
import com.taro.aimentor.api.RestClient
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.ConversationManager
import com.taro.aimentor.conversation.TextOptionsDialog
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.settings.SettingsActivity
import com.taro.aimentor.speech.TextToSpeechManager
import com.taro.aimentor.util.ClipboardUtil
import com.taro.aimentor.util.UIUtil

class MainActivity : AppCompatActivity(), RestClient.Listener,
    ConversationAdapter.Listener, TextToSpeechManager.Listener, TextOptionsDialog.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var conversationManager: ConversationManager
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var restClient: RestClient
    private lateinit var textToSpeechManager: TextToSpeechManager
    private lateinit var textOptionsDialog: TextOptionsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferencesManager = PreferencesManager.getInstance(this)
        if (preferencesManager.logAppOpenAndCheckForRatingUpsell()) {
            AlertDialog.Builder(this)
                .setMessage(R.string.please_rate)
                .setNegativeButton(R.string.no_im_good) { _: DialogInterface, _: Int -> }
                .setPositiveButton(R.string.will_rate) { _: DialogInterface, _: Int ->
                    val uri = Uri.parse("market://details?id=$packageName")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (packageManager.queryIntentActivities(intent, 0).size <= 0) {
                        UIUtil.showLongToast(R.string.play_store_error, this)
                        return@setPositiveButton
                    }
                    startActivity(intent)
                }
                .show()
        }

        conversationManager = ConversationManager()
        restClient = RestClient(listener = this)
        textToSpeechManager = TextToSpeechManager(
            context = this,
            listener = this,
            locale = resources.configuration.locales.get(0)
        )
        textOptionsDialog = TextOptionsDialog(
            context = this,
            listener = this
        )

        conversationAdapter = ConversationAdapter(listener = this)
        binding.conversationList.adapter = conversationAdapter
        bindComposer()

        val rootView = findViewById<View>(android.R.id.content).rootView
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

            restClient.getChatGPTResponse(
                conversation = conversationManager.getOnlyCompleteMessages(context = this)
            )
        }
    }

    override fun onResponseFetched(response: String) {
        conversationManager.onChatGPTResponseReturned(response = response)
        val updatedConversation = conversationManager.getAllMessages()
        conversationAdapter.submitList(updatedConversation)
        conversationAdapter.notifyItemChanged(conversationAdapter.itemCount - 1)
    }

    override fun onResponseFailure() {
        UIUtil.showLongToast(
            stringId = R.string.chatgpt_error,
            context = this
        )
    }

    override fun onMessageClicked(message: ChatMessage) {
        textOptionsDialog.show(message = message)
    }

    override fun onCopyMessageClicked(message: ChatMessage) {
        ClipboardUtil.copyTextToClipboard(
            textToCopy = message.content,
            label = getString(R.string.ai_mentor_text_label),
            context = this
        )
        UIUtil.showShortToast(
            stringId = R.string.text_successfully_copied,
            context = this
        )
    }

    override fun onShareMessageClicked(message: ChatMessage) {
        val shareIntent = ShareCompat.IntentBuilder(this)
            .setChooserTitle(R.string.share_text_with)
            .setType("text/plain")
            .setText(message.content)
            .intent
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(shareIntent)
        }
    }

    override fun onSpeakMessageClicked(message: ChatMessage) {
        textToSpeechManager.speak(text = message.content)
    }

    override fun onTextToSpeechFailure() {
        UIUtil.showLongToast(
            stringId = R.string.tts_failure_message,
            context = this
        )
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in)
    }

    override fun finish() {
        restClient.cleanUp(activity = this)
        textToSpeechManager.shutdown()
        textOptionsDialog.cleanUp()
        super.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        UIUtil.loadMenuIcon(
            menu = menu!!,
            itemId = R.id.stop_speaking,
            icon = MaterialDesignIconic.Icon.gmi_volume_off,
            context = this
        )
        UIUtil.loadMenuIcon(
            menu = menu,
            itemId = R.id.settings,
            icon = MaterialDesignIconic.Icon.gmi_settings,
            context = this
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.stop_speaking -> {
                textToSpeechManager.stopSpeaking()
                return true
            }
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
