package eu.catlabs.demo.dto.auth;

public record AuthResponse(String token, long expiresInSeconds) {}
