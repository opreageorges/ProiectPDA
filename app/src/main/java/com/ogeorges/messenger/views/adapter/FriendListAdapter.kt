package com.ogeorges.messenger.views.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ogeorges.messenger.R
import com.ogeorges.messenger.repositories.UserInfo
import com.ogeorges.messenger.views.FriendListFragmentDirections
import com.ogeorges.messenger.views.viewHolers.FriendVH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FriendListAdapter(friendList : ArrayList<String>,
                        private val coroutineScope: CoroutineScope,
                        private val navController: NavController) :
    RecyclerView.Adapter<FriendVH>(){

    var friendList = friendList
    @SuppressLint("NotifyDataSetChanged")
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendVH {
        val context = parent.context
        return FriendVH(LayoutInflater.from(context).inflate(R.layout.friend_list_adapter, parent, false))
    }

    private fun remove(friendRequest: String){
        val index = friendList.indexOf(friendRequest)
        friendList.remove(friendRequest)
        notifyItemRemoved(index)
    }

    override fun onBindViewHolder(holder: FriendVH, position: Int) {
        val friend = friendList[position]
        holder.friendName.text = friend
        holder.deleteButton.setOnClickListener {
            coroutineScope.launch {
                UserInfo.deleteFriend(friend)
            }
            remove(friend)
        }
        holder.chatButton.setOnClickListener{
            val args = Bundle()
            args.putString("friendUsername", friend)
            val action = FriendListFragmentDirections.actionFriendListFragmentToMessageRoomFragment(friend)
            navController.navigate(action)
        }

        holder.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, friend)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            it.context.startActivity(shareIntent)
        }

    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}