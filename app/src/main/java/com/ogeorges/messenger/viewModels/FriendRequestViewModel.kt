package com.ogeorges.messenger.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ogeorges.messenger.repositories.Authenticator
import com.ogeorges.messenger.repositories.UserInfo
import com.ogeorges.messenger.views.adapter.FriendRequestAdapter
import kotlinx.coroutines.launch

class FriendRequestViewModel : ViewModel() {

    private val friendRequests = MutableLiveData<List<String>?>()
    var getFriendRequests: LiveData<List<String>?> = friendRequests

    fun addFriend(friendUsername: String){
        viewModelScope.launch {
            UserInfo.friendRequestFromTo(Authenticator.currentUsername!!, friendUsername)
        }
    }

    fun getAllFriendRequests(){
        viewModelScope.launch {
            val currUser = UserInfo.getUser(Authenticator.currentUsername!!)
            friendRequests.postValue(currUser!!.requestList)
        }
    }

    fun filterAdapterData(adapter: FriendRequestAdapter, filter: String) {
        val trimmedFilter = filter.trim()
        if (trimmedFilter == ""){
            adapter.clearFilter()
            return
        }
        adapter.filterData(trimmedFilter)
    }
}