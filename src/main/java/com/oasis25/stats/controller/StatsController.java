package com.oasis25.stats.controller;

import com.oasis25.stats.dto.EmotionStatsResponse;
import com.oasis25.stats.dto.TrendStatsResponse;
import com.oasis25.stats.dto.WeatherFocusStatsResponse;
import com.oasis25.stats.dto.WeeklyLogResponse;
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

    @Operation(summary = "시간대별 몰입 분석", description = "오늘을 포함한 최근 7일간 완료된 뽀모도로 로그를 시간대별로 조회합니다.")
    @GetMapping("/weekly-logs")
    public ResponseEntity<List<WeeklyLogResponse>> getWeeklyLogs() {
        return ResponseEntity.ok(statsService.getWeeklyLogs());
    }

    @Operation(summary = "날씨별 몰입도 비교", description = "최근 30일간 날씨 조건별 평균 집중 시간을 조회합니다.")
    @GetMapping("/weather")
    public ResponseEntity<List<WeatherFocusStatsResponse>> getWeatherStats() {
        return ResponseEntity.ok(statsService.getWeatherStats());
    }

    @Operation(summary = "최근 30일 감정 흐름", description = "최근 30일간 일기의 일자별 감정 점수를 조회합니다.")
    @GetMapping("/emotions")
    public ResponseEntity<List<EmotionStatsResponse>> getEmotionStats() {
        return ResponseEntity.ok(statsService.getEmotionStats());
    }

    @Operation(summary = "30일 성장 트렌드", description = "최근 30일간 일자별 총 집중 시간을 조회합니다.")
    @GetMapping("/trend")
    public ResponseEntity<List<TrendStatsResponse>> getTrend() {
        return ResponseEntity.ok(statsService.getTrend());
    }
}
