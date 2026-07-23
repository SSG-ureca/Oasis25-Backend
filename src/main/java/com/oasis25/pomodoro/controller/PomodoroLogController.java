package com.oasis25.pomodoro.controller;

import com.oasis25.pomodoro.dto.PomodoroHeatmapResponse;
import com.oasis25.pomodoro.dto.PomodoroLogCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroLogResponse;
import com.oasis25.pomodoro.service.PomodoroLogService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pomodoro")
@RequiredArgsConstructor
@Tag(name = "Pomodoro Log", description = "뽀모도로 로그 API")
public class PomodoroLogController {

    private final PomodoroLogService pomodoroLogService;

    @Operation(summary = "뽀모도로 로그 생성", description = "새로운 뽀모도로 세션 로그를 생성합니다.")
    @PostMapping
    public ResponseEntity<PomodoroLogResponse> create(@Valid @RequestBody PomodoroLogCreateRequest request) {
        return ResponseEntity.ok(pomodoroLogService.create(request));
    }

    @Operation(summary = "뽀모도로 로그 완료", description = "특정 뽀모도로 세션을 완료 처리합니다.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<PomodoroLogResponse> complete(
            @Parameter(description = "뽀모도로 로그 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pomodoroLogService.complete(id));
    }

    @Operation(summary = "뽀모도로 로그 날짜별 조회", description = "특정 날짜의 뽀모도로 로그를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PomodoroLogResponse>> getByDate(
            @Parameter(description = "조회할 날짜", example = "2026-07-20") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pomodoroLogService.findByDate(date));
    }

    @Operation(summary = "집중 시간 Heatmap 조회", description = "특정 연도의 일별 집중 시간 히트맵을 조회합니다.")
    @GetMapping("/heatmap")
    public ResponseEntity<List<PomodoroHeatmapResponse>> getHeatmap(
            @Parameter(description = "조회할 연도", example = "2026") @RequestParam int year) {
        return ResponseEntity.ok(pomodoroLogService.getHeatmap(year));
    }
}
