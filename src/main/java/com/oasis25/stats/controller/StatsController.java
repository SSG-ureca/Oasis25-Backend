package com.oasis25.stats.controller;

import com.oasis25.stats.dto.WeatherFocusStatsResponse;
import com.oasis25.stats.service.StatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "통계 API")
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "날씨별 집중 통계 조회", description = "날씨 조건별 뽀모도로 집중 통계를 조회합니다.")
    @GetMapping("/weather")
    public ResponseEntity<List<WeatherFocusStatsResponse>> getWeatherStats() {
        return ResponseEntity.ok(statsService.getWeatherStats());
    }
}
