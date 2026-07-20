package com.oasis25.water.controller;

import com.oasis25.water.dto.WaterCaffeineLogCreateRequest;
import com.oasis25.water.dto.WaterCaffeineLogResponse;
import com.oasis25.water.entity.WaterCaffeineLogType;
import com.oasis25.water.service.WaterCaffeineLogService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/water-caffeine")
@RequiredArgsConstructor
@Tag(name = "Water Caffeine Log", description = "물/카페인 섭취 기록 API")
public class WaterCaffeineLogController {

    private final WaterCaffeineLogService waterCaffeineLogService;

    @Operation(summary = "물/카페인 기록 생성", description = "새로운 물 또는 카페인 섭취 기록을 생성합니다.")
    @PostMapping
    public ResponseEntity<WaterCaffeineLogResponse> create(@Valid @RequestBody WaterCaffeineLogCreateRequest request) {
        return ResponseEntity.ok(waterCaffeineLogService.create(request));
    }

    @Operation(summary = "물/카페인 기록 날짜별 조회", description = "특정 날짜의 물/카페인 기록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WaterCaffeineLogResponse>> getByDate(
            @Parameter(description = "조회할 날짜", example = "2026-07-20") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(waterCaffeineLogService.findByDate(date));
    }

    @Operation(summary = "물/카페인 합계 조회", description = "특정 날짜의 물 또는 카페인 섭취 합계를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<Integer> getSummary(
            @Parameter(description = "조회할 날짜", example = "2026-07-20") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "조회할 타입", example = "WATER") @RequestParam WaterCaffeineLogType type) {
        return ResponseEntity.ok(waterCaffeineLogService.getTotalByDateAndType(date, type));
    }

    @Operation(summary = "물/카페인 기록 삭제", description = "특정 물/카페인 기록을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "기록 ID", example = "1") @PathVariable Long id) {
        waterCaffeineLogService.delete(id);
        return ResponseEntity.ok().build();
    }
}
