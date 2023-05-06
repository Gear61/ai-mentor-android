package com.taro.aimentor.speech

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.core.content.ContextCompat
import com.taro.aimentor.R
import com.taro.aimentor.databinding.SpeechToTextDialogBinding

class SpeechToTextDialog(
    activity: Activity,
    val listener: Listener
): DialogInterface.OnDismissListener {

    interface Listener {

        fun onDialogDismissed()

        fun onTryAgainClicked()
    }

    private var binding: SpeechToTextDialogBinding
    private val dialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(activity)
        binding = SpeechToTextDialogBinding.inflate(activity.layoutInflater)
        builder.setView(binding.root)
        dialog = builder.create()
        dialog.window!!.attributes.windowAnimations = R.style.speech_dialog_animation
        dialog.setOnDismissListener(this)

        binding.tryAgain.setOnClickListener {
            listener.onTryAgainClicked()
        }
    }

    override fun onDismiss(dialogInterface: DialogInterface?) {
        listener.onDialogDismissed()
    }

    fun maybeShowDialog() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun changeUIStateToListening() {
        binding.voiceIcon.setTextColor(ContextCompat.getColor(dialog.context, R.color.white))
        binding.voiceIcon.setBackgroundResource(R.drawable.green_button)
        binding.message.setText(R.string.chatgpt_speech_prompt)
        binding.tryAgain.visibility = View.GONE
    }

    fun changeUIStateToRetry() {
        binding.voiceIcon.setTextColor(ContextCompat.getColor(dialog.context, R.color.green))
        binding.voiceIcon.setBackgroundResource(R.drawable.red_ring_background)
        binding.message.setText(R.string.did_not_catch_speech)
        binding.tryAgain.visibility = View.VISIBLE
    }

    fun setMessage(message: String) {
        binding.message.text = message
    }
}
