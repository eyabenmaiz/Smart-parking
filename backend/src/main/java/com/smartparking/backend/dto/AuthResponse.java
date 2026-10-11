package com.smartparking.backend.dto;

public record AuthResponse(String token, UserResponse user) {
}
