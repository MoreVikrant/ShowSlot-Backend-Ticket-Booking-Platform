package com.ShowSlot.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds,
                           ProfileResponse profile) {
}
