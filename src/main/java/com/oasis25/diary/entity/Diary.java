package com.oasis25.diary.entity;

import com.oasis25.common.entity.BaseUpdatableEntity;
import com.oasis25.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "diary_date" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "emotion_score")
    private Integer emotionScore;

    public static Diary create(User user, LocalDate diaryDate, String content, Integer emotionScore) {
        return Diary.builder()
                .user(user)
                .diaryDate(diaryDate)
                .content(content)
                .emotionScore(emotionScore)
                .build();
    }

    public void update(String content, Integer emotionScore) {
        this.content = content;
        this.emotionScore = emotionScore;
    }

    public void updateAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}
