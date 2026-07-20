package com.oasis25.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherFocusStatsResponse {

    @Schema(description = "날씨 상태", example = "맑음")
    private String weatherCondition;

    @Schema(description = "전체 세션 수", example = "10")
    private Long totalSessions;

    @Schema(description = "완료 세션 수", example = "8")
    private Long completedSessions;

    @Schema(description = "완료율", example = "0.8")
    private Double completionRate;

    @Schema(description = "평균 집중 시간(분)", example = "25.5")
    private Double avgFocusMinutes;
}
