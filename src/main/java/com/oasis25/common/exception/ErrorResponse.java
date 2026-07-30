package com.oasis25.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INVALID_INPUT")
    private final String code;

    @Schema(description = "에러 메시지", example = "입력값이 올바르지 않습니다.")
    private final String message;

    @Schema(description = "에러 발생 시간", example = "2026-07-20T10:00:00")
    private final String timestamp;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
