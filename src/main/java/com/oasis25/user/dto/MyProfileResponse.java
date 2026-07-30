package com.oasis25.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyProfileResponse {

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "동띵")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://i.ibb.co/abc123/profile.png")
    private String profileImage;
}
