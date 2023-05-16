package com.taro.aimentor.conversation

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.taro.aimentor.R
import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.models.MessageState
import com.taro.aimentor.models.MessageType
import com.taro.aimentor.util.UIUtil

open class ConversationAdapter(
    val listener: Listener
) : ListAdapter<ChatMessage, ViewHolder>(TaskDiffCallBack()) {

    interface Listener {
        fun onMessageClicked(message: ChatMessage)

        fun onCopyMessageClicked(message: ChatMessage)

        fun onShareMessageClicked(message: ChatMessage)

        fun onSpeakMessageClicked(message: ChatMessage)
    }

    class TaskDiffCallBack : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.getType() == newItem.getType()
                    && oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.getType() == newItem.getType()
                    && oldItem.content == newItem.content
        }
    }

    companion object {
        const val MESSAGE_VIEW_TYPE = 0
        const val LOADING_THOUGHTS_VIEW_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).getState()) {
            MessageState.COMPLETE -> MESSAGE_VIEW_TYPE
            MessageState.LOADING -> LOADING_THOUGHTS_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            MESSAGE_VIEW_TYPE -> {
                return MessageViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.message_list_item,
                        parent,
                        false
                    )
                )
            }
            LOADING_THOUGHTS_VIEW_TYPE -> {
                return LoadingThoughtsViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.loading_thoughts_message_item,
                        parent,
                        false
                    )
                )
            }
            else -> error("Unsupported message view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItem(position).getState()) {
            MessageState.COMPLETE -> (holder as MessageViewHolder).bind(position = position)
            MessageState.LOADING -> {}
        }
    }

    inner class MessageViewHolder(itemView: View) : ViewHolder(itemView) {
        private val startFiller: View = itemView.findViewById(R.id.start_filler)
        private val endFiller: View = itemView.findViewById(R.id.end_filler)
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val actionButtons: View = itemView.findViewById(R.id.message_action_buttons)
        private val copyButton: View = itemView.findViewById(R.id.copy_message_button)
        private val shareButton: View = itemView.findViewById(R.id.share_message_button)
        private val speakButton: View = itemView.findViewById(R.id.speak_message_button)

        fun bind(position: Int) {
            val chatMessage = getItem(position)
            when (chatMessage.getType()) {
                MessageType.USER -> {
                    startFiller.visibility = View.VISIBLE
                    endFiller.visibility = View.GONE
                    val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END
                    }
                    messageText.layoutParams = params
                    messageText.setBackgroundResource(R.drawable.user_message_background)
                    messageText.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_title))

                    val actionButtonParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END
                    }
                    actionButtons.layoutParams = actionButtonParams
                }
                MessageType.ASSISTANT -> {
                    startFiller.visibility = View.GONE
                    endFiller.visibility = View.VISIBLE

                    val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                    messageText.layoutParams = params
                    messageText.setBackgroundResource(R.drawable.assistant_message_background)
                    messageText.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

                    val actionButtonParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                    actionButtons.layoutParams = actionButtonParams
                }
                else -> {
                    error("Unsupported message type to render for ConversationAdapter")
                }
            }
            UIUtil.getMarkwonInstance(context = itemView.context)
                .setMarkdown(messageText, chatMessage.content)
            messageText.setOnClickListener {
                listener.onMessageClicked(message = chatMessage)
            }
            copyButton.setOnClickListener {
                listener.onCopyMessageClicked(message = chatMessage)
            }
            shareButton.setOnClickListener {
                listener.onShareMessageClicked(message = chatMessage)
            }
            speakButton.setOnClickListener {
                listener.onSpeakMessageClicked(message = chatMessage)
            }
        }
    }

    inner class LoadingThoughtsViewHolder(itemView: View) : ViewHolder(itemView)
}
