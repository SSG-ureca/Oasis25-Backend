package com.oasis25.user.dto;

import com.oasis25.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "동띵")
    private String nickname;

    @Schema(description = "역할", example = "ROLE_USER")
    private UserRole role;
}
