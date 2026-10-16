package com.example.smartparking.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.DELETE

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/parkings")
    suspend fun getParkings(): List<ParkingResponse>

    @GET("api/parkings/{id}")
    suspend fun getParking(@Path("id") id: Long): ParkingResponse

    @GET("api/parkings/{id}/spots")
    suspend fun getSpots(@Path("id") id: Long): List<SpotResponse>

    @POST("api/reservations")
    suspend fun reserve(@Body request: ReservationRequest): ReservationResponse

    @GET("api/reservations/me")
    suspend fun getMyReservations(): List<ReservationResponse>

    @DELETE("api/reservations/{id}")
    suspend fun cancelReservation(@Path("id") id: Long): Response<Unit>
}