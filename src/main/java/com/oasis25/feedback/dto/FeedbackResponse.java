package com.oasis25.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackResponse {

    @Schema(description = "피드백 ID", example = "1")
    private Long id;

    @Schema(description = "긍정 피드백 여부", example = "true")
    private boolean isGood;

    @Schema(description = "피드백 내용", example = "좋은 피드백입니다.")
    private String content;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;
}
