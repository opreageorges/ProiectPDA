package com.ogeorges.messenger.views.viewHolers

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R

class FriendVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val friendName: TextView
    val chatButton: Button
    val deleteButton: Button
    val shareButton: ImageButton

    init {
        friendName = itemView.findViewById(R.id.friendListAdapterUsername)
        chatButton = itemView.findViewById(R.id.friendListAdapterChat)
        deleteButton = itemView.findViewById(R.id.friendListAdapterDelete)
        shareButton = itemView.findViewById(R.id.friendListAdapterShare)
    }
}