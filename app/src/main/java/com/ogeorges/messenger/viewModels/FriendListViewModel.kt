package com.ogeorges.messenger.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ogeorges.messenger.repositories.Authenticator
import com.ogeorges.messenger.repositories.UserInfo
import kotlinx.coroutines.launch

class FriendListViewModel : ViewModel() {

    private val friends = MutableLiveData<List<String>?>()
    var getFriends: LiveData<List<String>?> = friends

    fun logout(sharedPreferences: SharedPreferences){
        Authenticator.logout(sharedPreferences)
        UserInfo.resetCurrentUser()
    }

    fun getAllFriends(){
        viewModelScope.launch {
            val currUser = UserInfo.getUser(Authenticator.currentUsername!!)
            friends.postValue(currUser!!.friendList)
        }
    }


}