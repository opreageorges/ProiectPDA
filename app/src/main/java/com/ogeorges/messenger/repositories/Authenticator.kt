package com.ogeorges.messenger.repositories

import android.content.SharedPreferences
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object Authenticator {

    private val auth = Firebase.auth
    var currentUsername: String? = null
    private set

    fun isLoggedIn(): Boolean{
        return auth.currentUser != null
    }

    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun verifyForGoogleLogin(username: String, email: String): Boolean{
        val db = Firebase.firestore

        val usernameTaken = db.collection("Userdata").document(username).get().await()

        if(!usernameTaken.exists()){
            val addToDB = db.collection("Userdata").document(username).set(mapOf(
                "email" to email,
                "friend_list" to listOf<String>(),
                "friend_requests" to listOf<String>()))
            addToDB.await()
            return true
        }

        if (usernameTaken["email"] != email){
            return false
        }

        return true

    }

    suspend fun loginWithGoogle(credential: AuthCredential, username: String, sharedPreferences: SharedPreferences): Boolean{
        return try {
            auth.signInWithCredential(credential).await()

            currentUsername = username
            sharedPreferences.edit().putString("username", currentUsername).apply()
            true
        }catch (e: java.lang.Exception){
            false
        }

    }

    suspend fun registerUser(email: String, password: String, username: String): Boolean {
        val alreadyLoggedIn = auth.fetchSignInMethodsForEmail(email).await()


        if (alreadyLoggedIn.signInMethods != null && alreadyLoggedIn.signInMethods!!.size > 0){
            return false
        }

        val db = Firebase.firestore

        val usernameTaken = db.collection("Userdata").document(username).get().await()
        if (usernameTaken["email"] != null){
            return false
        }

        val register = auth.createUserWithEmailAndPassword(email, password)

        register.await()
        val addToDB = db.collection("Userdata").document(username).set(mapOf(
            "email" to email,
            "friend_list" to listOf<String>(),
            "friend_requests" to listOf<String>()))
        addToDB.await()

        return register.isSuccessful && addToDB.isSuccessful
    }

    fun alreadyLoggedIn(sharedPreferences: SharedPreferences){
        currentUsername = sharedPreferences.getString("username", null)
    }


    suspend fun login(username: String, password: String, sharedPreferences: SharedPreferences): Boolean {
        if (username == "" || password == "") return false

        val db = Firebase.firestore

        val email = db.collection("Userdata").document(username).get()
        email.await()

        if (email.result == null || email.result["email"] == null) return false

        val login = auth.signInWithEmailAndPassword(email.result["email"] as String, password)
        login.await()

        if (login.isSuccessful){
            currentUsername = username
            sharedPreferences.edit().putString("username", currentUsername).apply()
        }

        return login.isSuccessful
    }

    fun logout(sharedPreferences: SharedPreferences){
        sharedPreferences.edit().remove("username").apply()
        auth.signOut()
    }
}