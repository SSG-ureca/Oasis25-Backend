package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FocusCategoryResponse {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "업무")
    private String name;

    @Schema(description = "카테고리 색상", example = "#FF5733")
    private String color;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;
}
