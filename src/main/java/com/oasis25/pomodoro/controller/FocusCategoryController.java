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

@RestController
@RequestMapping("/api/pomodoro/categories")
@RequiredArgsConstructor
public class FocusCategoryController {

    private final FocusCategoryService focusCategoryService;

    @PostMapping
    public ResponseEntity<FocusCategoryResponse> create(@Valid @RequestBody FocusCategoryCreateRequest request) {
        return ResponseEntity.ok(focusCategoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FocusCategoryResponse>> getAll() {
        return ResponseEntity.ok(focusCategoryService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        focusCategoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
