package com.oasis25.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    @Schema(description = "일반 사용자")
    ROLE_USER("일반 사용자"),
    @Schema(description = "관리자")
    ROLE_ADMIN("관리자");

    private final String description;
}
