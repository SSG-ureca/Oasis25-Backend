package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroHeatmapResponse {

    @Schema(description = "날짜", example = "2026-01-01")
    private LocalDate date;

    @Schema(description = "집중 시간(분)", example = "120")
    private Integer focusMinutes;
}
