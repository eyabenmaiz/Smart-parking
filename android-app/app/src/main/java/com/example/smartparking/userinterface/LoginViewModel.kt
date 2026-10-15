package com.example.smartparking.userinterface

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.LoginRequest
import com.example.smartparking.data.TokenStore
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val tokenStore = TokenStore(app)

    var message by mutableStateOf("")
        private set
    var loggedIn by mutableStateOf(tokenStore.get() != null)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.login(LoginRequest(email.trim(), password))
                tokenStore.save(response.token)
                loggedIn = true
                message = ""
            } catch (e: Exception) {
                message = "Échec de la connexion : ${e.message}"
            }
        }
    }

    fun logout() {
        tokenStore.clear()
        loggedIn = false
        message = ""
    }
}