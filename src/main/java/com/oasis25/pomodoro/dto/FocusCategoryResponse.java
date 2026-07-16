package com.oasis25.pomodoro.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FocusCategoryResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
