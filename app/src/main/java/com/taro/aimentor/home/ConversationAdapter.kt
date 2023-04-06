package com.taro.aimentor.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taro.aimentor.R
import com.taro.aimentor.models.ChatMessage

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
        private val messageText: View = itemView.findViewById(R.id.message_text)

        fun bind(position: Int) {
        }
    }
}
