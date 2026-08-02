package com.applix.shop.auth;

public record LoginResponse(String token, String login, String role) {
}
