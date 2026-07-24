package com.oasis25.pomodoro.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pomodoro_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PomodoroLog extends BaseCreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FocusCategory category;

    @Column(nullable = false)
    private Integer focusMinutes;

    @Column(nullable = false)
    private Integer breakMinutes;

    @Column(nullable = false)
    private boolean completed;

    @Column
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "weather_condition")
    private WeatherCondition weatherCondition;

    @Column
    private Double temperature;

    @Builder.Default
    @Column(nullable = false)
    private Integer elapsedFocusSeconds = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer elapsedBreakSeconds = 0;

    public static PomodoroLog create(User user, FocusCategory category, Integer focusMinutes, Integer breakMinutes,
            WeatherCondition weatherCondition, Double temperature) {
        return PomodoroLog.builder()
                .user(user)
                .category(category)
                .focusMinutes(focusMinutes)
                .breakMinutes(breakMinutes)
                .completed(false)
                .weatherCondition(weatherCondition)
                .temperature(temperature)
                .build();
    }

    public static PomodoroLog create(User user, FocusCategory category, Integer focusMinutes, Integer breakMinutes) {
        return create(user, category, focusMinutes, breakMinutes, null, null);
    }

    public void addElapsedSeconds(int focusSeconds, int breakSeconds) {
        this.elapsedFocusSeconds += focusSeconds;
        this.elapsedBreakSeconds += breakSeconds;
    }

    public void complete() {
        this.completed = true;
        this.endTime = LocalDateTime.now();
    }
}
