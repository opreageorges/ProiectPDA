package com.ogeorges.messenger.viewModels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.ogeorges.messenger.R
import com.ogeorges.messenger.repositories.Authenticator
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val authResult = MutableLiveData<Int>()
    var getAuthResult: LiveData<Int> = authResult

    private val googleAuthResult = MutableLiveData<Int>()
    var getGoogleAuthResult: LiveData<Int> = googleAuthResult

    var username: String = ""
    // -1 loading state, 0 rejected, 1 accepted
    fun login(username: String, password: String, sharedPreferences: SharedPreferences){
        authResult.postValue(-1)

        if (username.length < 5 || password.length < 5){
            authResult.postValue(0)
            return
        }

        viewModelScope.launch{

            try {
                val x = Authenticator.login(username,password, sharedPreferences)
                authResult.postValue(if(x) 1 else 0)
            }catch (e : FirebaseAuthInvalidCredentialsException){
                authResult.postValue(0)
            }

        }.start()
    }

    fun loginWithGoogle(context: Context, fragment: Fragment, username: String){
        if (username.trim() == "" || username.length < 5){
            this.googleAuthResult.postValue(-1)
            return
        }
        this.username = username

        Authenticator.logout(context.getSharedPreferences("user", Context.MODE_PRIVATE))
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
        requestEmail().
        requestIdToken(context.getString(R.string.default_web_client_id)).build()
        val googleClient = GoogleSignIn.getClient(context, options)

        fragment.startActivityForResult(googleClient.signInIntent, 420)
    }

    fun loginWithGoogleResult(data: Intent?, code: Int, sharedPreferences: SharedPreferences) {

        if (code != -1 || data == null){
            googleAuthResult.postValue(-3)
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            viewModelScope.launch{
                if(!Authenticator.verifyForGoogleLogin(username, account.email!!)){
                    googleAuthResult.postValue(-1)
                    return@launch
                }

                if(Authenticator.loginWithGoogle(credential, username, sharedPreferences)){
                    googleAuthResult.postValue(0)
                }else{
                    googleAuthResult.postValue(-2)
                }
            }

        } catch (e: ApiException) {
            googleAuthResult.postValue(-2)
        }
    }
}