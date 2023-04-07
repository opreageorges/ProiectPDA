package com.ogeorges.messenger.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.ogeorges.messenger.repositories.Authenticator
import com.ogeorges.messenger.R
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {

    private val _registerForm = MutableLiveData<Map<String, Int?>>()
    val getRegisterForm: LiveData<Map<String, Int?>> = _registerForm

    private val registerResult = MutableLiveData<Int>()
    var getRegisterResult: LiveData<Int> = registerResult

    init {
        this._registerForm.value = mapOf(Pair("username", null),
            Pair("password", null),
            Pair("email", null),
            Pair("ok", 0),)

    }

    fun registerDataChanged(username: String,email: String, password: String){
        val usernameMessage = if (checkUsername(username)) null else R.string.invalid_username
        val emailMessage = if (checkEmail(email)) null else R.string.invalid_email
        val passwordMessage = if (checkPassword(password)) null else R.string.invalid_password
        val okMessage = if (emailMessage != null || passwordMessage != null  || usernameMessage != null ) 0 else 1

        this._registerForm.postValue(mapOf(Pair("username", usernameMessage),
            Pair("password", passwordMessage),
            Pair("email", emailMessage),
            Pair("ok", okMessage)))
    }

    private fun checkUsername(username: String): Boolean{
        return username.length > 5
    }

    private fun checkEmail(email: String): Boolean{
        return Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$").matches(email)
    }

    private fun checkPassword(pass: String): Boolean{
        return Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$").matches(pass)

    }

    // -1 loading state, 0 rejected, 1 accepted
    fun onRegister(username: String, email: String, password: String) {
        registerResult.postValue(-1)
        if(this._registerForm.value!!["ok"] != 1){
            this._registerForm.postValue(mapOf(Pair("username", null),
                Pair("password", null),
                Pair("email", null),
                Pair("ok", 0)))
            registerResult.postValue(0)
            return
        }
        viewModelScope.launch{
            try {
                val x = Authenticator.registerUser(email, password, username)
                registerResult.postValue(if(x) 1 else 0)
            }catch (e : FirebaseAuthInvalidCredentialsException){
                registerResult.postValue(0)
            }

        }.start()

    }
}