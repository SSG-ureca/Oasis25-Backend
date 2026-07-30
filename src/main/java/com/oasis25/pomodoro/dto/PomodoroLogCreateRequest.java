package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PomodoroLogCreateRequest {

    @Schema(description = "집중 카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "집중 시간(분)", example = "25")
    @NotNull
    @Min(1)
    private Integer focusMinutes;

    @Schema(description = "휴식 시간(분)", example = "5")
    @NotNull
    @Min(1)
    private Integer breakMinutes;

    @Schema(description = "날씨 상태", example = "맑음")
    private String weatherCondition;

    @Schema(description = "기온", example = "24.5")
    private Double temperature;
}
