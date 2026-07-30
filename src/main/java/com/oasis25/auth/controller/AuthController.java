package com.oasis25.auth.controller;

import com.oasis25.auth.dto.AuthResult;
import com.oasis25.auth.dto.AuthStatusResponse;
import com.oasis25.auth.dto.LoginRequest;
import com.oasis25.auth.dto.LogoutRequest;
import com.oasis25.auth.dto.ReissueRequest;
import com.oasis25.auth.dto.RegisterRequest;
import com.oasis25.auth.dto.TokenResponse;
import com.oasis25.auth.service.AuthService;
import com.oasis25.auth.util.AuthCookieUtil;
import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.JwtTokenProvider;
import com.oasis25.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 API")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthStatusResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        AuthResult result = authService.login(request);
        TokenResponse tokenResponse = result.getTokenResponse();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.createAccessTokenCookie(tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(),
                        servletRequest).toString());
        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.createRefreshTokenCookie(tokenResponse.getRefreshToken(),
                        jwtTokenProvider.getRefreshTokenExpiration(), servletRequest).toString());

        return ResponseEntity.ok(new AuthStatusResponse(tokenResponse.getExpiresIn(), result.getUser()));
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    @PostMapping("/reissue")
    public ResponseEntity<AuthStatusResponse> reissue(HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        String refreshToken = AuthCookieUtil.extractRefreshToken(servletRequest);
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        ReissueRequest reissueRequest = new ReissueRequest();
        reissueRequest.setRefreshToken(refreshToken);

        AuthResult result = authService.reissue(reissueRequest);
        TokenResponse tokenResponse = result.getTokenResponse();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.createAccessTokenCookie(tokenResponse.getAccessToken(), tokenResponse.getExpiresIn(),
                        servletRequest).toString());
        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.createRefreshTokenCookie(tokenResponse.getRefreshToken(),
                        jwtTokenProvider.getRefreshTokenExpiration(), servletRequest).toString());

        return ResponseEntity.ok(new AuthStatusResponse(tokenResponse.getExpiresIn(), result.getUser()));
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 삭제하여 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String refreshToken = AuthCookieUtil.extractRefreshToken(servletRequest);
        if (refreshToken != null) {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setRefreshToken(refreshToken);
            authService.logout(logoutRequest);
        }

        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.expireAccessTokenCookie(servletRequest).toString());
        servletResponse.addHeader(HttpHeaders.SET_COOKIE,
                AuthCookieUtil.expireRefreshTokenCookie(servletRequest).toString());

        return ResponseEntity.ok().build();
    }
}
