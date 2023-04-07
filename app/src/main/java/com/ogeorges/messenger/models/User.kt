package com.ogeorges.messenger.models

data class User(val username:String, val friendList: ArrayList<String>, val requestList: ArrayList<String>)
