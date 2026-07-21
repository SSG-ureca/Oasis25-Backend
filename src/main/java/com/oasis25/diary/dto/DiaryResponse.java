package com.oasis25.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryResponse {

    @Schema(description = "일기 ID", example = "1")
    private Long id;

    @Schema(description = "일기 날짜", example = "2026-07-20")
    private LocalDate diaryDate;

    @Schema(description = "일기 내용", example = "오늘은 좋은 하루였다.")
    private String content;

    @Schema(description = "AI 요약", example = "오늘은 긍정적인 하루였습니다.")
    private String aiSummary;

    @Schema(description = "감정 점수", example = "4")
    private Integer emotionScore;

    @Schema(description = "첨부파일 URL", example = "https://i.ibb.co/abc123/attachment.png")
    private String attachmentUrl;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-07-20T12:00:00")
    private LocalDateTime updatedAt;
}
