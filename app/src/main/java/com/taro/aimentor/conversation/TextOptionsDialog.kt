package com.taro.aimentor.conversation

import android.app.AlertDialog
import android.content.Context
import com.taro.aimentor.R
import com.taro.aimentor.models.ChatMessage

class TextOptionsDialog(
    context: Context,
    var listener: Listener?
) {

    interface Listener {
        fun onCopyMessageClicked(message: ChatMessage)

        fun onShareMessageClicked(message: ChatMessage)

        fun onSpeakMessageClicked(message: ChatMessage)
    }

    private var chatMessage: ChatMessage? = null
    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.text_options_dialog_title)
        builder.setItems(R.array.conversation_text_options) { _, which ->
            when (which) {
                0 -> listener?.onCopyMessageClicked(message = chatMessage!!)
                1 -> listener?.onShareMessageClicked(message = chatMessage!!)
                2 -> listener?.onSpeakMessageClicked(message = chatMessage!!)
            }
        }

        dialog = builder.create()
    }

    fun show(message: ChatMessage) {
        chatMessage = message
        dialog.show()
    }

    fun cleanUp() {
        listener = null
    }
}
