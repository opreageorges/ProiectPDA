package com.ogeorges.messenger.views.adapter

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R
import com.ogeorges.messenger.models.Message
import com.ogeorges.messenger.views.viewHolers.MessageVH

class MessageAdapter(messageList: ArrayList<Message>) : RecyclerView.Adapter<MessageVH>() {
    var messageList : ArrayList<Message> = messageList
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            value.sortBy { Integer.parseInt(it.index) }
            field = value
            notifyDataSetChanged()
        }

    init {
        this.messageList = ArrayList(messageList)
        messageList.sortBy { Integer.parseInt(it.index) }
    }

    fun lastIndex(): Int{
        return messageList.size
    }

    fun add(message: Message){
        messageList.add(message)
        notifyItemInserted(messageList.indexOf(message))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        val context = parent.context
        return MessageVH(LayoutInflater.from(context).inflate(R.layout.message_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {
        val message = messageList[position]
        holder.messageBubble.text = message.body

        val params: FrameLayout.LayoutParams = if (!message.from) {
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.END)
        } else{
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.START)
        }
        params.setMargins(10)
        holder.messageBubble.layoutParams = params
    }
}