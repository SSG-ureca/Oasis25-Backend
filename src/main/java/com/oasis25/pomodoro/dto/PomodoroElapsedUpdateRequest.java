package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PomodoroElapsedUpdateRequest {

    @Schema(description = "누적 집중 시간(초)", example = "1500")
    @NotNull
    @Min(0)
    private Integer elapsedFocusSeconds;

    @Schema(description = "누적 휴식 시간(초)", example = "300")
    @NotNull
    @Min(0)
    private Integer elapsedBreakSeconds;
}
