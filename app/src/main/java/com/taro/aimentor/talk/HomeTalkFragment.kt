package com.taro.aimentor.talk

import androidx.fragment.app.Fragment

class HomeTalkFragment: Fragment() {

    companion object {

        fun getInstance(): HomeTalkFragment {
            return HomeTalkFragment()
        }

        private const val CONVERSATION_MESSAGES = "conversation_messages"
    }
}
