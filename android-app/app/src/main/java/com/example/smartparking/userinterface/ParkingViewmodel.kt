package com.example.smartparking.userinterface

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.ParkingResponse
import kotlinx.coroutines.launch

class ParkingsViewModel : ViewModel() {

    var parkings by mutableStateOf<List<ParkingResponse>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                parkings = ApiClient.api.getParkings()
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }
}