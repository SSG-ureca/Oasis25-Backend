package com.oasis25.pomodoro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PomodoroLogCreateRequest {

    private Long categoryId;

    @NotNull
    @Min(1)
    private Integer focusMinutes;

    @NotNull
    @Min(1)
    private Integer breakMinutes;
}
