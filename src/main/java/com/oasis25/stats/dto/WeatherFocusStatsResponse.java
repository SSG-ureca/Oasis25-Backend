package com.oasis25.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherFocusStatsResponse {

    @Schema(description = "날씨 상태", example = "비")
    private String weatherCondition;

    @Schema(description = "평균 집중 시간(분)", example = "45.2")
    private Double avgFocusMinutes;
}
