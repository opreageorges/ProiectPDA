package com.ogeorges.messenger

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ogeorges.messenger.databinding.ActivityMainBinding
import com.ogeorges.messenger.repositories.Authenticator

// Pentru a functiona e nevoie de firebase: Authentication si Firestore Databse cu o colectie "Userdata"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val authenticator = Authenticator

    private val debug = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel.alreadyLoggedIn(getSharedPreferences("user", Context.MODE_PRIVATE))

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val graph = viewModel.generateGraphBasedOnUser(navController)
        navController.graph = graph

        binding.fab.visibility = if(!debug) View.GONE else View.VISIBLE

        binding.fab.setOnClickListener { view ->
            if (authenticator.isLoggedIn()) {
                Snackbar.make(view, authenticator.currentUser()!!.uid + authenticator.currentUser()!!.email, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}