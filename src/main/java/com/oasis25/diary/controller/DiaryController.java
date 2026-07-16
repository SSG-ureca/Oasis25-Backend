package com.oasis25.diary.controller;

import com.oasis25.diary.dto.DiaryCreateRequest;
import com.oasis25.diary.dto.DiaryResponse;
import com.oasis25.diary.dto.DiaryUpdateRequest;
import com.oasis25.diary.service.DiaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@Tag(name = "Diary", description = "일기 API")
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 생성", description = "새로운 일기를 생성합니다.")
    @PostMapping
    public ResponseEntity<DiaryResponse> create(@Valid @RequestBody DiaryCreateRequest request) {
        return ResponseEntity.ok(diaryService.create(request));
    }

    @Operation(summary = "일기 조회", description = "특정 날짜의 일기를 조회합니다.")
    @GetMapping
    public ResponseEntity<DiaryResponse> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(diaryService.getByDate(date));
    }

    @Operation(summary = "일기 수정", description = "일기를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DiaryUpdateRequest request) {
        return ResponseEntity.ok(diaryService.update(id, request));
    }

    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diaryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "AI 요약 생성", description = "일기의 AI 요약을 생성합니다.")
    @PostMapping("/{id}/ai-summary")
    public ResponseEntity<DiaryResponse> generateAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.generateAiSummary(id));
    }
}
