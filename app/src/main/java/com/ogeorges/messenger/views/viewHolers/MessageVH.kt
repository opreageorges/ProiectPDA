package com.ogeorges.messenger.views.viewHolers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R

class MessageVH(itemView: View): RecyclerView.ViewHolder(itemView) {
    val messageBubble: TextView

    init {
        messageBubble = itemView.findViewById(R.id.messageAdapterMessageBubble)
    }
}