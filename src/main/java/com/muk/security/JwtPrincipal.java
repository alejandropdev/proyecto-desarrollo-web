package com.muk.security;

public record JwtPrincipal(String subject, AuthRole role, Long userId) {
}
