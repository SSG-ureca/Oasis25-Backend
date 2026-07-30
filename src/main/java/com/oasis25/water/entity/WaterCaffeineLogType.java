package com.oasis25.water.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public enum WaterCaffeineLogType {
    @Schema(description = "물")
    WATER,
    @Schema(description = "카페인")
    CAFFEINE
}
