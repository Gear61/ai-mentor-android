package com.taro.aimentor.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {

    fun copyTextToClipboard(
        textToCopy: String,
        label: String,
        context: Context
    ) {
        val clipboard = context.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)
    }
}
