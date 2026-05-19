package com.muk.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthCookieService {

    private final String cookieName;
    private final long expirationSeconds;
    private final boolean secure;

    public AuthCookieService(
            @Value("${auth.jwt.cookie-name:AuthToken}") String cookieName,
            @Value("${auth.jwt.expiration-seconds:86400}") long expirationSeconds,
            @Value("${auth.jwt.cookie-secure:false}") boolean secure) {
        this.cookieName = cookieName;
        this.expirationSeconds = expirationSeconds;
        this.secure = secure;
    }

    public Optional<String> readToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    public void addTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = baseCookie(token)
                .maxAge(expirationSeconds)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = baseCookie("")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax");
    }
}
