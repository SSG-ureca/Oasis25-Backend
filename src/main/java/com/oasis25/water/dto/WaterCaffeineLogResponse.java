package com.oasis25.water.dto;

import com.oasis25.water.entity.WaterCaffeineLogType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaterCaffeineLogResponse {

    @Schema(description = "기록 ID", example = "1")
    private Long id;

    @Schema(description = "로그 타입", example = "WATER")
    private WaterCaffeineLogType logType;

    @Schema(description = "섭취량", example = "250")
    private Integer amount;

    @Schema(description = "생성 일시", example = "2026-07-20T10:00:00")
    private LocalDateTime createdAt;
}
