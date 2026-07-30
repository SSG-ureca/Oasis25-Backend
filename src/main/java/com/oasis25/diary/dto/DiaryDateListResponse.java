package com.oasis25.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryDateListResponse {

    @Schema(description = "회고(일기)가 작성된 날짜 목록", example = "[\"2026-07-20\", \"2026-07-21\"]")
    private List<LocalDate> dates;
}
