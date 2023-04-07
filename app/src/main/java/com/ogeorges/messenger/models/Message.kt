package com.ogeorges.messenger.models

import com.google.firebase.Timestamp

data class Message(val body: String, val timestamp: Timestamp, val index:String, val from: Boolean)