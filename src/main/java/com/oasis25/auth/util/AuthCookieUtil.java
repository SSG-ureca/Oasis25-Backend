package com.oasis25.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import org.springframework.http.ResponseCookie;

public final class AuthCookieUtil {

    private static final String ACCESS_TOKEN_NAME = "accessToken";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private static final String PATH = "/";

    private AuthCookieUtil() {
    }

    public static ResponseCookie createAccessTokenCookie(String accessToken, long maxAgeMillis, HttpServletRequest request) {
        return createTokenCookie(ACCESS_TOKEN_NAME, accessToken, maxAgeMillis, request);
    }

    public static ResponseCookie createRefreshTokenCookie(String refreshToken, long maxAgeMillis, HttpServletRequest request) {
        return createTokenCookie(REFRESH_TOKEN_NAME, refreshToken, maxAgeMillis, request);
    }

    public static ResponseCookie expireAccessTokenCookie(HttpServletRequest request) {
        return expireTokenCookie(ACCESS_TOKEN_NAME, request);
    }

    public static ResponseCookie expireRefreshTokenCookie(HttpServletRequest request) {
        return expireTokenCookie(REFRESH_TOKEN_NAME, request);
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static ResponseCookie createTokenCookie(String name, String token, long maxAgeMillis, HttpServletRequest request) {
        boolean secure = request.isSecure();
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, token)
                .httpOnly(true)
                .path(PATH)
                .maxAge(Duration.ofMillis(maxAgeMillis));
        if (secure) {
            builder.sameSite("None")
                    .secure(true);
        } else {
            builder.sameSite("Lax");
        }
        return builder.build();
    }

    private static ResponseCookie expireTokenCookie(String name, HttpServletRequest request) {
        boolean secure = request.isSecure();
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .path(PATH)
                .maxAge(Duration.ZERO);
        if (secure) {
            builder.sameSite("None")
                    .secure(true);
        } else {
            builder.sameSite("Lax");
        }
        return builder.build();
    }
}
