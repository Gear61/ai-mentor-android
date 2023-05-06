package com.taro.aimentor.speech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.taro.aimentor.util.StringUtil
import java.util.*

class SpeechToTextManager(
    private var activity: Activity?,
    private val listener: Listener
) : RecognitionListener, SpeechToTextDialog.Listener {

    interface Listener {
        fun onTextSpoken(spokenText: String)
    }

    // This is lazily instantiated and is also nulled out when the user dismisses the prompt without speaking
    private var speechRecognizer: SpeechRecognizer? = null
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private val speechToTextDialog: SpeechToTextDialog

    init {
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault().language
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechToTextDialog = SpeechToTextDialog(
            activity = activity!!,
            listener = this
        )
    }

    fun startSpeechToTextFlow() {
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
        speechRecognizer!!.setRecognitionListener(this)
        speechRecognizer!!.startListening(speechRecognizerIntent)
        speechToTextDialog.changeUIStateToListening()
        speechToTextDialog.maybeShowDialog()
    }

    override fun onBeginningOfSpeech() {}
    override fun onBufferReceived(buffer: ByteArray) {}

    override fun onEndOfSpeech() {
        speechRecognizer!!.stopListening()
    }

    override fun onError(error: Int) {
        speechToTextDialog.changeUIStateToRetry()
    }

    override fun onEvent(eventType: Int, params: Bundle) {}

    override fun onPartialResults(partialResults: Bundle) {
        val data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (data != null && data.isNotEmpty()) {
            val rawTranscription = data[data.size - 1]
            val latestPartialTranscription = StringUtil.capitalizeSentences(
                locale = activity!!.resources.configuration.locales.get(0),
                input = rawTranscription
            )
            speechToTextDialog.setMessage(message = latestPartialTranscription)
        }
    }

    override fun onReadyForSpeech(params: Bundle) {}

    override fun onResults(results: Bundle) {
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val rawTranscription = matches[0]
            val finalTranscription = StringUtil.capitalizeSentences(
                locale = activity!!.resources.configuration.locales.get(0),
                input = rawTranscription
            )
            speechToTextDialog.setMessage(message = finalTranscription)
            listener.onTextSpoken(finalTranscription)
            speechToTextDialog.dismiss()
        } else {
            speechToTextDialog.changeUIStateToRetry()
        }
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onDialogDismissed() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    override fun onTryAgainClicked() {
        startSpeechToTextFlow()
    }

    fun cleanUp() {
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
        }
        activity = null
    }
}
