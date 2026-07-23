package com.oasis25.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklyLogResponse {

    @Schema(description = "뽀모도로 완료 시각", example = "2026-07-23T01:10:15")
    private LocalDateTime createdAt;

    @Schema(description = "집중 시간(분)", example = "25")
    private Integer focusMinutes;
}
