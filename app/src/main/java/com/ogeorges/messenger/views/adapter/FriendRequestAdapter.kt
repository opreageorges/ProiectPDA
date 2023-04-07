package com.ogeorges.messenger.views.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R
import com.ogeorges.messenger.repositories.UserInfo
import com.ogeorges.messenger.views.viewHolers.FriendRequestVH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class FriendRequestAdapter(friendRequests : ArrayList<String>, private val coroutineScope: CoroutineScope) : RecyclerView.Adapter<FriendRequestVH>(){

    private var allFriendRequests: ArrayList<String> = ArrayList()

    var friendRequests : ArrayList<String> = ArrayList()

        set(value) {
            field.clear()
            field.addAll(value)
            allFriendRequests.clear()
            allFriendRequests.addAll(value)
            notifyDataSetChanged()
        }

    init {
        this.allFriendRequests.addAll(friendRequests)
        this.friendRequests.addAll(friendRequests)
    }

    private fun remove(friendRequest: String){
        val index = friendRequests.indexOf(friendRequest)
        friendRequests.remove(friendRequest)
        allFriendRequests.remove(friendRequest)
        notifyItemRemoved(index)
    }

    fun filterData(filter: String){
        clearFilter()
        val removedIndexes = ArrayList<Int>()
        val trimmedFilter = filter.trim()
        for (elem in friendRequests){
            if (!elem.contains(Regex(".*$trimmedFilter.*"))) removedIndexes.add(friendRequests.indexOf(elem))
        }

        for (index in removedIndexes.reversed()){
            friendRequests.removeAt(index)
            notifyItemRemoved(index)
        }

    }

    fun clearFilter(){
        if (friendRequests.size == allFriendRequests.size) {
            return
        }
        else {
            friendRequests = ArrayList(allFriendRequests)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestVH {
        val context = parent.context
        return FriendRequestVH(LayoutInflater.from(context).inflate(R.layout.friend_request_adapter, parent, false))
    }

    override fun onBindViewHolder(holder: FriendRequestVH, position: Int) {
        val friendRequest = friendRequests[position]
        holder.friendName.text = friendRequest
        holder.acceptButton.setOnClickListener{
            remove(friendRequest)
            coroutineScope.launch {
                UserInfo.acceptRequestFrom(holder.friendName.text.toString())
            }

        }
        holder.declineButton.setOnClickListener{
            remove(friendRequest)
            coroutineScope.launch {
                UserInfo.declineRequestFrom(holder.friendName.text.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return friendRequests.size
    }
}