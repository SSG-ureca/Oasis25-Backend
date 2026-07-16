package com.oasis25.water.dto;

import com.oasis25.water.entity.WaterCaffeineLogType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaterCaffeineLogCreateRequest {

    @NotNull
    private WaterCaffeineLogType logType;

    @NotNull
    @Min(1)
    private Integer amount;
}
