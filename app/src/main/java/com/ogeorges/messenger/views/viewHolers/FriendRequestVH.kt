package com.ogeorges.messenger.views.viewHolers

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R

class FriendRequestVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val acceptButton: Button
    val declineButton: Button
    val friendName: TextView

    init {
        acceptButton = itemView.findViewById(R.id.friendRequestAdapterAccept)
        declineButton = itemView.findViewById(R.id.friendRequestAdapterDecline)
        friendName = itemView.findViewById(R.id.friendRequestAdapterUsername)
    }

}