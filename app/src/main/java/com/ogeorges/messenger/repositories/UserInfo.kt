@file:Suppress("UNCHECKED_CAST")

package com.ogeorges.messenger.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ogeorges.messenger.models.Message
import com.ogeorges.messenger.models.User
import kotlinx.coroutines.tasks.await

object UserInfo {
    private var currentUser: User? = null

    suspend fun getUser(username: String): User?{
        if (username == Authenticator.currentUsername && currentUser != null) return currentUser
        val db = Firebase.firestore
        val userinfo = db.collection("Userdata").document(username).get().await()

        if (userinfo["email"] == null){
            return null
        }

        val friends = userinfo["friend_list"] as List<String>
        val friendRequests = userinfo["friend_requests"] as List<String>

        val out = User(username, ArrayList(friends) , ArrayList(friendRequests))

        if(out.username == Authenticator.currentUsername) currentUser = out
        return out

    }

    fun resetCurrentUser(){
        currentUser = null
    }

    suspend fun getMessagesOfWith(ofUserUsername: String, withUserUsername: String): List<Message>?{
        val db = Firebase.firestore
        val userinfo = db.collection("Userdata").document(ofUserUsername).get().await()

        if (userinfo["email"] == null){
            return null
        }

        val out = ArrayList<Message>()

        if (userinfo[withUserUsername] == null){
            db.collection("Userdata").document(ofUserUsername).update(withUserUsername, mapOf("0" to listOf<Any>("", Timestamp.now(), false)))
            return null
        }

        val allMessages = userinfo[withUserUsername] as Map<String, List<Any>>
        allMessages.forEach { elem ->
            if(elem.key == "0") return@forEach
            val message = Message(elem.value[0] as String, elem.value[1] as Timestamp, elem.key, elem.value[2] as Boolean)
            out.add(message)
        }

        return out
    }

     suspend fun sendMessageTo(toUserUsername: String, message: Message){
         val db = Firebase.firestore

         val userInfo = db.collection("Userdata").document(Authenticator.currentUsername!!).get()
         userInfo.await()
         val messages = (userInfo.result[toUserUsername] as Map<String, MutableList<Any>>).toMutableMap()
         messages[message.index] = mutableListOf<Any>(message.body, message.timestamp, message.from)

         val friendInfo = db.collection("Userdata").document(toUserUsername).get()
         friendInfo.await()

         val friendMessages: MutableMap<String, MutableList<Any>> = messages.toMutableMap()
         messages.forEach{
             friendMessages[it.key] = it.value.toMutableList()
             friendMessages[it.key]!![2] = !(friendMessages[it.key]!![2] as Boolean)
         }
//         friendMessages.forEach{
//             val sender = !(it.value[2] as Boolean)
//             it.value[2] = sender
//         }

         db.collection("Userdata").
         document(Authenticator.currentUsername!!).update(toUserUsername, messages)

         db.collection("Userdata").
         document(toUserUsername).update(Authenticator.currentUsername!!, friendMessages)
    }

    suspend fun friendRequestFromTo(fromUserUsername: String, toUserUsername: String): Boolean{
        val db = Firebase.firestore
        val userinfo = db.collection("Userdata").document(toUserUsername).get().await()

        if (userinfo["email"] == null){
            return false
        }

        for (elem in listOf(userinfo["friend_requests"])){
            if (elem != null) {
                if (elem == fromUserUsername){
                    return false
                }
            }
        }

        for (elem in listOf(userinfo["friend_list"])){
            if (elem != null) {
                if (elem == fromUserUsername){
                    return false
                }
            }
        }

        db.collection("Userdata").document(toUserUsername).update("friend_requests", FieldValue.arrayUnion(fromUserUsername))
        return true
    }

    suspend fun acceptRequestFrom(fromUserUsername: String){
        val db = Firebase.firestore

        if (currentUser == null) return

        currentUser!!.requestList.remove(fromUserUsername)
        if(currentUser!!.friendList.contains(fromUserUsername)) return
        db.collection("Userdata").document(currentUser!!.username).update("friend_requests", FieldValue.arrayRemove(fromUserUsername)).await()


        currentUser!!.friendList.add(fromUserUsername)
        db.collection("Userdata").document(currentUser!!.username).update("friend_list", FieldValue.arrayUnion(fromUserUsername)).await()

        db.collection("Userdata").document(fromUserUsername).update("friend_list", FieldValue.arrayUnion(currentUser!!.username)).await()
    }

    suspend fun declineRequestFrom(fromUserUsername: String) {
        val db = Firebase.firestore

        if (currentUser == null) return

        currentUser!!.requestList.remove(fromUserUsername)

        db.collection("Userdata").document(currentUser!!.username).update("friend_requests", FieldValue.arrayRemove(fromUserUsername)).await()
    }

    suspend fun deleteFriend(friendUsername: String) {
        val db = Firebase.firestore

        if (currentUser == null) return

        currentUser!!.friendList.remove(friendUsername)

        db.collection("Userdata").document(currentUser!!.username).update("friend_list", FieldValue.arrayRemove(friendUsername)).await()
        db.collection("Userdata").document(friendUsername).update("friend_list", FieldValue.arrayRemove(currentUser!!.username)).await()
    }

}