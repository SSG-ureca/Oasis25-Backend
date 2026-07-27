package com.oasis25.auth.dto;

import com.oasis25.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthStatusResponse {
    private Long expiresIn;
    private UserResponse user;
}
