package com.oasis25.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackCreateRequest {

    @Schema(description = "좋은 피드백 여부", example = "true")
    @NotNull
    private Boolean isGood;

    @Schema(description = "피드백 내용", example = "좋은 피드백입니다.")
    @NotBlank
    private String content;
}
