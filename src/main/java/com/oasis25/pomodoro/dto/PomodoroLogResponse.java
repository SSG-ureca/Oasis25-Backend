package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroLogResponse {

    @Schema(description = "로그 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "업무")
    private String categoryName;

    @Schema(description = "집중 시간(분)", example = "25")
    private Integer focusMinutes;

    @Schema(description = "휴식 시간(분)", example = "5")
    private Integer breakMinutes;

    @Schema(description = "완료 여부", example = "true")
    private boolean completed;

    @Schema(description = "종료 일시", example = "2026-07-20T10:25:00")
    private LocalDateTime endTime;

    @Schema(description = "날씨 상태", example = "맑음")
    private String weatherCondition;

    @Schema(description = "기온", example = "24.5")
    private Double temperature;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;
}
