package com.example.smartparking.data

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)

data class UserResponse(val id: Long, val name: String, val email: String)

data class AuthResponse(val token: String, val user: UserResponse)