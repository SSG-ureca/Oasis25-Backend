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

@RestController
@RequestMapping("/api/water-caffeine")
@RequiredArgsConstructor
public class WaterCaffeineLogController {

    private final WaterCaffeineLogService waterCaffeineLogService;

    @PostMapping
    public ResponseEntity<WaterCaffeineLogResponse> create(@Valid @RequestBody WaterCaffeineLogCreateRequest request) {
        return ResponseEntity.ok(waterCaffeineLogService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<WaterCaffeineLogResponse>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(waterCaffeineLogService.findByDate(date));
    }

    @GetMapping("/summary")
    public ResponseEntity<Integer> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam WaterCaffeineLogType type) {
        return ResponseEntity.ok(waterCaffeineLogService.getTotalByDateAndType(date, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waterCaffeineLogService.delete(id);
        return ResponseEntity.ok().build();
    }
}
