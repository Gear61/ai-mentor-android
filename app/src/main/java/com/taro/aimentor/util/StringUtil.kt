package com.taro.aimentor.util

import android.icu.text.BreakIterator
import java.util.*

object StringUtil {

    fun capitalizeSentences(
        locale: Locale,
        input: String
    ): String {
        val iterator = BreakIterator.getSentenceInstance(locale)
        iterator.setText(input)
        var start = iterator.first()
        var end = iterator.next()
        val output = StringBuilder()
        while (end != BreakIterator.DONE) {
            val sentence = input.substring(start, end)
            output.append(sentence.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() })
            start = end
            end = iterator.next()
        }
        return output.toString()
    }
}
