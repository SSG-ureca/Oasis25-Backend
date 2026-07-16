package com.oasis25.pomodoro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FocusCategoryCreateRequest {

    @NotBlank
    private String name;

    private String color;
}
