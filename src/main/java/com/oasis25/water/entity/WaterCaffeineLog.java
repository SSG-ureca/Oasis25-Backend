package com.oasis25.water.entity;

import com.oasis25.common.entity.BaseCreatedEntity;
import com.oasis25.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "water_caffeine_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WaterCaffeineLog extends BaseCreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WaterCaffeineLogType logType;

    @Column(nullable = false)
    private Integer amount;

    public static WaterCaffeineLog create(User user, WaterCaffeineLogType logType, Integer amount) {
        return WaterCaffeineLog.builder()
                .user(user)
                .logType(logType)
                .amount(amount)
                .build();
    }
}
