package com.oasis25.feedback.controller;

import com.oasis25.feedback.dto.FeedbackCreateRequest;
import com.oasis25.feedback.dto.FeedbackResponse;
import com.oasis25.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "피드백 API")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 생성", description = "새로운 피드백을 생성합니다.")
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackCreateRequest request) {
        return ResponseEntity.ok(feedbackService.create(request));
    }
}
