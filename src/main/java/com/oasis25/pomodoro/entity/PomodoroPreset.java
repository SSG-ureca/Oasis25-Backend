package com.oasis25.pomodoro.entity;

import com.oasis25.common.entity.BaseCreatedEntity;
import com.oasis25.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "pomodoro_preset")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PomodoroPreset extends BaseCreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer focusMinutes;

    @Column(nullable = false)
    private Integer breakMinutes;

    @Column(nullable = false)
    private boolean isDefault;

    public static PomodoroPreset create(User user, String name, Integer focusMinutes, Integer breakMinutes, boolean isDefault) {
        return PomodoroPreset.builder()
                .user(user)
                .name(name)
                .focusMinutes(focusMinutes)
                .breakMinutes(breakMinutes)
                .isDefault(isDefault)
                .build();
    }

    public void update(String name, Integer focusMinutes, Integer breakMinutes) {
        this.name = name;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
    }
}
