package com.oasis25.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrendStatsResponse {

    @Schema(description = "날짜", example = "2026-07-23")
    private LocalDate date;

    @Schema(description = "총 집중 시간(분)", example = "150")
    private Integer totalFocusMinutes;
}
