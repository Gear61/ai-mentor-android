package com.taro.aimentor.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.taro.aimentor.R

class ProgressDialog(context: Context) {

    private val dialog: AlertDialog
    private val progressText: TextView

    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        progressText = dialogView.findViewById(R.id.progress_text)
        dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
    }

    fun show(progressMessageResId: Int) {
        progressText.setText(progressMessageResId)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
