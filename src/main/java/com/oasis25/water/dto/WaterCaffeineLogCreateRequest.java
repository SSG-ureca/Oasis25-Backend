package com.oasis25.water.dto;

import com.oasis25.water.entity.WaterCaffeineLogType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterCaffeineLogCreateRequest {

    @Schema(description = "로그 타입 (WATER/CAFFEINE)", example = "WATER")
    @NotNull
    private WaterCaffeineLogType logType;

    @Schema(description = "섭취량", example = "250")
    @NotNull
    @Min(1)
    private Integer amount;
}
