package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroPresetResponse {

    @Schema(description = "프리셋 ID", example = "1")
    private Long id;

    @Schema(description = "프리셋 이름", example = "기본 프리셋")
    private String name;

    @Schema(description = "집중 시간(분)", example = "25")
    private Integer focusMinutes;

    @Schema(description = "휴식 시간(분)", example = "5")
    private Integer breakMinutes;

    @Schema(description = "기본 프리셋 여부", example = "false")
    private boolean isDefault;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;
}
