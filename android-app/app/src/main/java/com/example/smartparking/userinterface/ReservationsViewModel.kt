package com.example.smartparking.userinterface



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.ReservationResponse
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {

    var reservations by mutableStateOf<List<ReservationResponse>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var message by mutableStateOf("")
        private set

    fun load() {
        viewModelScope.launch {
            loading = true
            try {
                reservations = ApiClient.api.getMyReservations()
                message = ""
            } catch (e: Exception) {
                message = "Erreur : ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    fun cancel(id: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.cancelReservation(id)
                message = when {
                    response.isSuccessful -> "Réservation annulée"
                    response.code() == 403 -> "Cette réservation n'est pas la vôtre"
                    response.code() == 404 -> "Réservation introuvable"
                    else -> "Erreur du serveur (${response.code()})"
                }
                if (response.isSuccessful) reservations = ApiClient.api.getMyReservations()
            } catch (e: Exception) {
                message = "Erreur réseau : ${e.message}"
            }
        }
    }
}