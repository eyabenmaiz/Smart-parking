package com.example.smartparking.data

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)

data class UserResponse(val id: Long, val name: String, val email: String)

data class AuthResponse(val token: String, val user: UserResponse)
data class ParkingResponse(
    val id: Long,
    val name: String,
    val address: String,
    val capacity: Int,
    val availableSpots: Int
)
data class SpotResponse(
    val id: Long,
    val number: String,
    val available: Boolean
)

data class ReservationRequest(
    val spotId: Long,
    val startTime: String,
    val endTime: String
)

data class ReservationResponse(
    val id: Long,
    val spotId: Long,
    val spotNumber: String,
    val parkingName: String,
    val startTime: String,
    val endTime: String,
    val status: String
)