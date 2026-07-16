package com.oasis25.water.dto;

import com.oasis25.water.entity.WaterCaffeineLogType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WaterCaffeineLogResponse {

    private Long id;
    private WaterCaffeineLogType logType;
    private Integer amount;
    private LocalDateTime createdAt;
}
