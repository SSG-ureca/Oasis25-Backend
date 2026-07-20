package com.oasis25.pomodoro.controller;

import com.oasis25.pomodoro.dto.FocusCategoryCreateRequest;
import com.oasis25.pomodoro.dto.FocusCategoryResponse;
import com.oasis25.pomodoro.service.FocusCategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pomodoro/categories")
@RequiredArgsConstructor
@Tag(name = "Focus Category", description = "집중 카테고리 API")
public class FocusCategoryController {

    private final FocusCategoryService focusCategoryService;

    @Operation(summary = "집중 카테고리 생성", description = "새로운 집중 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<FocusCategoryResponse> create(@Valid @RequestBody FocusCategoryCreateRequest request) {
        return ResponseEntity.ok(focusCategoryService.create(request));
    }

    @Operation(summary = "집중 카테고리 전체 조회", description = "모든 집중 카테고리를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FocusCategoryResponse>> getAll() {
        return ResponseEntity.ok(focusCategoryService.findAll());
    }

    @Operation(summary = "집중 카테고리 삭제", description = "특정 집중 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "카테고리 ID", example = "1") @PathVariable Long id) {
        focusCategoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
