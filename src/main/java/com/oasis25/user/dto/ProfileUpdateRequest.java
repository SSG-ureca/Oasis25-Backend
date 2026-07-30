package com.oasis25.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {

    @Schema(description = "닉네임", example = "동띵")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}
