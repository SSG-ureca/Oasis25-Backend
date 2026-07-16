package com.oasis25.pomodoro.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PomodoroLogResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Integer focusMinutes;
    private Integer breakMinutes;
    private boolean completed;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
