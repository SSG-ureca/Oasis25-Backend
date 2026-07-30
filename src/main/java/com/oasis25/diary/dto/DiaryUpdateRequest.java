package com.oasis25.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryUpdateRequest {

    @Schema(description = "일기 내용", example = "오늘은 좋은 하루였다.")
    @NotBlank
    private String content;

    @Schema(description = "감정 점수", example = "4")
    @Min(1)
    @Max(5)
    private Integer emotionScore;
}
