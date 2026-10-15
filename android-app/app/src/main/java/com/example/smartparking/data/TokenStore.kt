package com.example.smartparking.data

import android.content.Context

class TokenStore(context: Context) {

    private val prefs = context.getSharedPreferences("smartparking", Context.MODE_PRIVATE)

    fun save(token: String) = prefs.edit().putString("token", token).apply()

    fun get(): String? = prefs.getString("token", null)

    fun clear() = prefs.edit().remove("token").apply()
}