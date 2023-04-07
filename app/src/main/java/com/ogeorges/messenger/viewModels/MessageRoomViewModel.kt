package com.ogeorges.messenger.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ogeorges.messenger.models.Message
import com.ogeorges.messenger.repositories.Authenticator
import com.ogeorges.messenger.repositories.UserInfo
import kotlinx.coroutines.launch

class MessageRoomViewModel : ViewModel() {
    var friendUsername: String =""

    private val messages = MutableLiveData<List<Message>?>()
    var getMessages: LiveData<List<Message>?> = messages


    fun getMessagesFromDB(){
        viewModelScope.launch {
            messages.postValue(UserInfo.getMessagesOfWith(Authenticator.currentUsername!!, friendUsername))
        }
    }

    fun sendMessage(message: Message){
        viewModelScope.launch {
            UserInfo.sendMessageTo(friendUsername, message)
        }
    }

}