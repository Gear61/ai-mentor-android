package com.taro.aimentor.speech

import android.annotation.TargetApi
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TextToSpeechManager(
    context: Context,
    private var listener: Listener?
) : OnInitListener {

    interface Listener {
        fun onTextToSpeechFailure()
    }

    private val textToSpeech: TextToSpeech
    private var enabled = false
    private val audioManager: AudioManager

    // Oreo audio focus shenanigans
    private var audioFocusRequest: AudioFocusRequest? = null

    init {
        textToSpeech = TextToSpeech(context, this)
        textToSpeech.language = Locale.getDefault()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initializeOAudioFocusParams()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun initializeOAudioFocusParams() {
        val ttsAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(ttsAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener, Handler())
            .build()
    }

    fun speak(text: String) {
        if (enabled) {
            textToSpeech.language = Locale("en")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestAudioFocusPostO(text)
            } else {
                requestAudioFocusPreO(text)
            }
        } else {
            listener!!.onTextToSpeechFailure()
        }
    }

    private fun playTts(text: String) {
        val utteranceId = this.hashCode().toString() + ""
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    private fun requestAudioFocusPreO(text: String) {
        val result = audioManager.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text)
        } else {
            listener!!.onTextToSpeechFailure()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun requestAudioFocusPostO(text: String) {
        val res = audioManager.requestAudioFocus(audioFocusRequest!!)
        if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playTts(text)
        }
    }

    override fun onInit(status: Int) {
        enabled = status == TextToSpeech.SUCCESS
        if (enabled) {
            setUtteranceListener()
        }
    }

    private fun setUtteranceListener() {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {}
            override fun onDone(utteranceId: String) {
                audioManager.abandonAudioFocus(audioFocusChangeListener)
            }

            override fun onError(utteranceId: String) {
                listener!!.onTextToSpeechFailure()
            }
        })
    }

    private val audioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange: Int ->
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
            ) {
                stopSpeaking()
            }
        }

    fun stopSpeaking() {
        if (textToSpeech.isSpeaking) {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
            textToSpeech.stop()
        }
    }

    fun shutdown() {
        textToSpeech.shutdown()
        listener = null
    }
}
