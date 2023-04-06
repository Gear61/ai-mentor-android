package com.taro.aimentor.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taro.aimentor.R
import com.taro.aimentor.models.ChatMessage
import com.taro.aimentor.models.MessageType

open class ConversationAdapter : ListAdapter<ChatMessage, ConversationAdapter.MessageViewHolder>(TaskDiffCallBack()) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.message_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(position = position)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startFiller: View = itemView.findViewById(R.id.start_filler)
        private val endFiller: View = itemView.findViewById(R.id.end_filler)
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(position: Int) {
            val chatMessage = getItem(position)
            when (chatMessage.getType()) {
                MessageType.USER -> {
                    startFiller.visibility = View.VISIBLE
                    endFiller.visibility = View.GONE
                    messageText.setBackgroundResource(R.drawable.user_message_background)
                }
                MessageType.ASSISTANT -> {
                    startFiller.visibility = View.GONE
                    endFiller.visibility = View.VISIBLE
                    messageText.setBackgroundResource(R.drawable.assistant_message_background)
                }
                else -> {
                    error("Unsupported message type to render for ConversationAdapter")
                }
            }
            messageText.text = chatMessage.content
        }
    }
}
