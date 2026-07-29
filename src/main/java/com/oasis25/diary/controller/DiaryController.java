package com.oasis25.diary.controller;

import com.oasis25.diary.dto.DiaryCreateRequest;
import com.oasis25.diary.dto.DiaryDateListResponse;
import com.oasis25.diary.dto.DiaryResponse;
import com.oasis25.diary.dto.DiaryUpdateRequest;
import com.oasis25.diary.service.DiaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            @Parameter(description = "조회할 날짜", example = "2026-07-20") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(diaryService.getByDate(date));
    }

    @Operation(summary = "기간별 일기 작성 날짜 조회", description = "지정한 기간 내 회고(일기)를 작성한 모든 날짜를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = DiaryDateListResponse.class)))
    @GetMapping("/dates")
    public ResponseEntity<DiaryDateListResponse> getDatesByRange(
            @Parameter(description = "조회 시작 날짜", example = "2026-07-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료 날짜(포함)", example = "2026-07-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(diaryService.getDiaryDatesByRange(startDate, endDate));
    }

    @Operation(summary = "일기 수정", description = "일기를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> update(
            @Parameter(description = "일기 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody DiaryUpdateRequest request) {
        return ResponseEntity.ok(diaryService.update(id, request));
    }

    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "일기 ID", example = "1") @PathVariable Long id) {
        diaryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "AI 요약 생성", description = "일기의 AI 요약을 생성합니다.")
    @PostMapping("/{id}/ai-summary")
    public ResponseEntity<DiaryResponse> generateAiSummary(
            @Parameter(description = "일기 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(diaryService.generateAiSummary(id));
    }

    @Operation(summary = "첨부파일 업로드", description = "일기 첨부파일을 Cloudinary에 업로드하고 반환된 URL을 일기 정보에 저장합니다.")
    @PostMapping(value = "/{id}/attachment", consumes = "multipart/form-data")
    public ResponseEntity<DiaryResponse> uploadAttachment(
            @Parameter(description = "일기 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "업로드할 첨부파일", schema = @Schema(type = "string", format = "binary")) @RequestPart("attachment") MultipartFile attachment) {
        return ResponseEntity.ok(diaryService.uploadAttachment(id, attachment));
    }
}
