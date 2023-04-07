package com.ogeorges.messenger

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.ogeorges.messenger.repositories.Authenticator

class MainViewModel: ViewModel() {

    fun alreadyLoggedIn(sharedPreferences: SharedPreferences){
        if (Authenticator.isLoggedIn()) Authenticator.alreadyLoggedIn(sharedPreferences)
    }

    fun generateGraphBasedOnUser(navController: NavController): NavGraph {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        if (Authenticator.isLoggedIn()){
            graph.setStartDestination(R.id.friendListFragment)
        }
        else{
            graph.setStartDestination(R.id.loginFragment)
        }
        return graph
    }
}