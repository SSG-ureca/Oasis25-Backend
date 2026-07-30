package com.oasis25.pomodoro.controller;

import com.oasis25.pomodoro.dto.PomodoroPresetCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroPresetResponse;
import com.oasis25.pomodoro.dto.PomodoroPresetUpdateRequest;
import com.oasis25.pomodoro.service.PomodoroPresetService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pomodoro/presets")
@RequiredArgsConstructor
@Tag(name = "Pomodoro Preset", description = "뽀모도로 프리셋 API")
public class PomodoroPresetController {

    private final PomodoroPresetService pomodoroPresetService;

    @Operation(summary = "뽀모도로 프리셋 전체 조회", description = "모든 뽀모도로 프리셋을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PomodoroPresetResponse>> getAll() {
        return ResponseEntity.ok(pomodoroPresetService.findAll());
    }

    @Operation(summary = "뽀모도로 프리셋 생성", description = "새로운 뽀모도로 프리셋을 생성합니다.")
    @PostMapping
    public ResponseEntity<PomodoroPresetResponse> create(@Valid @RequestBody PomodoroPresetCreateRequest request) {
        return ResponseEntity.ok(pomodoroPresetService.create(request));
    }

    @Operation(summary = "뽀모도로 프리셋 수정", description = "특정 뽀모도로 프리셋을 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<PomodoroPresetResponse> update(
            @Parameter(description = "프리셋 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PomodoroPresetUpdateRequest request) {
        return ResponseEntity.ok(pomodoroPresetService.update(id, request));
    }

    @Operation(summary = "뽀모도로 프리셋 삭제", description = "특정 뽀모도로 프리셋을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "프리셋 ID", example = "1") @PathVariable Long id) {
        pomodoroPresetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
