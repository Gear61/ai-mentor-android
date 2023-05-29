package com.taro.aimentor.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.taro.aimentor.R

class SetDisplayNameDialog(context: Context, val listener: Listener) {

    interface Listener {

        fun onNewNameSubmitted(newName: String)
    }

    private val dialog: AlertDialog
    private val textInput: EditText

    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.set_name_dialog, null)
        textInput = dialogView.findViewById(R.id.display_name_input)

        dialog = AlertDialog.Builder(context)
            .setMessage(R.string.set_name_prompt)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                listener.onNewNameSubmitted(newName = textInput.text.toString().trim())
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
    }

    fun show(currentName: String) {
        textInput.setText(currentName)
        textInput.setSelection(currentName.length)
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = currentName.isNotBlank()

        textInput.requestFocus()
        textInput.doAfterTextChanged { input ->
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = !input.isNullOrBlank()
        }

        // Open the keyboard after a delay because Android is weird
        Handler(Looper.getMainLooper()).postDelayed({
            val inputMethodManager =
                ContextCompat.getSystemService(dialog.context, InputMethodManager::class.java)
            inputMethodManager!!.showSoftInput(textInput, 0)
        }, 250L)
    }
}
