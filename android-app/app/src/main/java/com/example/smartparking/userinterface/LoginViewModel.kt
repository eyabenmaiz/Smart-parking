package com.example.smartparking.userinterface

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.LoginRequest
import com.example.smartparking.data.RegisterRequest
import com.example.smartparking.data.TokenStore
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val tokenStore = TokenStore(app)

    var message by mutableStateOf("")
        private set
    var loggedIn by mutableStateOf(tokenStore.get() != null)
        private set

    fun clearMessage() {
        message = ""
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.login(LoginRequest(email.trim(), password))
                tokenStore.save(response.token)
                loggedIn = true
                message = ""
            } catch (e: HttpException) {
                message = if (e.code() == 401) "Email ou mot de passe incorrect"
                else "Erreur du serveur (${e.code()})"
            } catch (e: Exception) {
                message = "Erreur réseau : ${e.message}"
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.length < 8) {
            message = "Nom, email et mot de passe (8 caractères minimum) obligatoires"
            return
        }
        viewModelScope.launch {
            try {
                ApiClient.api.register(RegisterRequest(name.trim(), email.trim(), password))
                val response = ApiClient.api.login(LoginRequest(email.trim(), password))
                tokenStore.save(response.token)
                loggedIn = true
                message = ""
            } catch (e: HttpException) {
                message = when (e.code()) {
                    409 -> "Cet email est déjà utilisé"
                    400 -> "Vérifiez les champs (email valide, mot de passe de 8 caractères minimum)"
                    else -> "Erreur du serveur (${e.code()})"
                }
            } catch (e: Exception) {
                message = "Erreur réseau : ${e.message}"
            }
        }
    }

    fun logout() {
        tokenStore.clear()
        loggedIn = false
        message = ""
    }
}