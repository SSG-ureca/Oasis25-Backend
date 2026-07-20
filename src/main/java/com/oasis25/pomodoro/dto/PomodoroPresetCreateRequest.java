package com.oasis25.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PomodoroPresetCreateRequest {

    @Schema(description = "프리셋 이름", example = "기본 프리셋")
    @NotBlank
    @Size(max = 50)
    private String name;

    @Schema(description = "집중 시간(분)", example = "25")
    @NotNull
    @Min(1)
    @Max(120)
    private Integer focusMinutes;

    @Schema(description = "휴식 시간(분)", example = "5")
    @NotNull
    @Min(1)
    @Max(60)
    private Integer breakMinutes;
}
