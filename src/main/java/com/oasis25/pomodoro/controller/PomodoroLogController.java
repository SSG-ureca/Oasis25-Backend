package com.oasis25.pomodoro.controller;

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

@RestController
@RequestMapping("/api/pomodoro")
@RequiredArgsConstructor
public class PomodoroLogController {

    private final PomodoroLogService pomodoroLogService;

    @PostMapping
    public ResponseEntity<PomodoroLogResponse> create(@Valid @RequestBody PomodoroLogCreateRequest request) {
        return ResponseEntity.ok(pomodoroLogService.create(request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<PomodoroLogResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(pomodoroLogService.complete(id));
    }

    @GetMapping
    public ResponseEntity<List<PomodoroLogResponse>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pomodoroLogService.findByDate(date));
    }
}
