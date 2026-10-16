package com.example.smartparking.userinterface



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.ParkingResponse
import com.example.smartparking.data.ReservationRequest
import com.example.smartparking.data.SpotResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ParkingDetailViewModel : ViewModel() {

    var parking by mutableStateOf<ParkingResponse?>(null)
        private set
    var spots by mutableStateOf<List<SpotResponse>>(emptyList())
        private set
    var message by mutableStateOf("")
        private set

    fun load(parkingId: Long) {
        viewModelScope.launch {
            try {
                parking = ApiClient.api.getParking(parkingId)
                spots = ApiClient.api.getSpots(parkingId)
            } catch (e: Exception) {
                message = "Erreur de chargement : ${e.message}"
            }
        }
    }

    fun reserve(parkingId: Long, spotId: Long?, startInput: String, durationInput: String) {
        val chosenSpot = spotId ?: run {
            message = "Choisissez une place"
            return
        }
        val hours = durationInput.trim().toLongOrNull()
        if (hours == null || hours <= 0) {
            message = "Durée invalide (nombre d'heures, ex. 2)"
            return
        }
        val start = try {
            LocalDateTime.parse(startInput.trim().replace(" ", "T"))
        } catch (e: DateTimeParseException) {
            message = "Format attendu : 2027-01-20 10:00"
            return
        }
        val end = start.plusHours(hours)
        val format = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        viewModelScope.launch {
            try {
                val result = ApiClient.api.reserve(
                    ReservationRequest(chosenSpot, start.format(format), end.format(format))
                )
                message = "Réservation confirmée : place ${result.spotNumber}, ${result.parkingName}"
                spots = ApiClient.api.getSpots(parkingId)
            } catch (e: HttpException) {
                message = when (e.code()) {
                    409 -> "Créneau déjà pris ou place hors service"
                    400 -> "Dates invalides (début dans le futur, fin après le début)"
                    404 -> "Place introuvable"
                    else -> "Erreur du serveur (${e.code()})"
                }
            } catch (e: Exception) {
                message = "Erreur réseau : ${e.message}"
            }
        }
    }
}