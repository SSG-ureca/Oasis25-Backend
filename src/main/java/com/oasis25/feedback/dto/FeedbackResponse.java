package com.oasis25.feedback.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackResponse {

    private Long id;
    private boolean isGood;
    private String content;
    private LocalDateTime createdAt;
}
