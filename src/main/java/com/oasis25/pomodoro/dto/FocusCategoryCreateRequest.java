package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusCategoryCreateRequest {

    @Schema(description = "카테고리 이름", example = "업무")
    @NotBlank
    private String name;

    @Schema(description = "카테고리 색상", example = "#FF5733")
    private String color;
}
