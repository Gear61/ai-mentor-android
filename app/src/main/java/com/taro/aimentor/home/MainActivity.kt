package com.taro.aimentor.home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.taro.aimentor.R
import com.taro.aimentor.conversation.ConversationAdapter
import com.taro.aimentor.conversation.TextOptionsDialog
import com.taro.aimentor.databinding.ActivityMainBinding
import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.persistence.PreferencesManager
import com.taro.aimentor.speech.TextToSpeechManager
import com.taro.aimentor.util.ClipboardUtil
import com.taro.aimentor.util.UIUtil

class MainActivity : AppCompatActivity(), ConversationAdapter.Listener,
    TextToSpeechManager.Listener, TextOptionsDialog.Listener,
    BottomNavigationView.Listener {

    companion object {
        private const val PREVIOUSLY_SELECTED_PAGE_ID = "previously_selected_page_id"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController: HomepageFragmentController
    private lateinit var textOptionsDialog: TextOptionsDialog

    lateinit var textToSpeechManager: TextToSpeechManager

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

        textToSpeechManager = TextToSpeechManager(
            context = this,
            listener = this,
            locale = resources.configuration.locales.get(0)
        )
        textOptionsDialog = TextOptionsDialog(
            context = this,
            listener = this
        )

        navigationController = HomepageFragmentController(supportFragmentManager, R.id.container)
        binding.bottomNavigation.setListener(this)
        if (savedInstanceState == null) {
            binding.bottomNavigation.setCurrentlySelected(R.id.chat_button)
        } else {
            navigationController.restoreFragments(savedInstanceState = savedInstanceState)
            val previousSelectedId = savedInstanceState.getInt(
                PREVIOUSLY_SELECTED_PAGE_ID,
                R.id.chat_button
            )
            navigationController.onNavItemSelected(previousSelectedId)
            binding.bottomNavigation.setCurrentlySelected(previousSelectedId)
        }
    }

    override fun onNavItemSelected(viewId: Int) {
        UIUtil.hideKeyboard(activity = this)
        navigationController.onNavItemSelected(viewId)

        when (viewId) {
            R.id.chat_button -> setTitle(R.string.app_name)
            R.id.talk_button -> setTitle(R.string.app_name)
            R.id.profile_button -> setTitle(R.string.profile)
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            PREVIOUSLY_SELECTED_PAGE_ID,
            navigationController.currentViewId
        )
        navigationController.saveFragments(outState = outState)
    }

    override fun finish() {
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.stop_speaking -> {
                textToSpeechManager.stopSpeaking()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
