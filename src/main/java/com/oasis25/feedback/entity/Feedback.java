package com.oasis25.feedback.entity;

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
@Table(name = "feedbacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Feedback extends BaseCreatedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean isGood;

    @Column(nullable = false, length = 1000)
    private String content;

    public static Feedback create(User user, boolean isGood, String content) {
        return Feedback.builder()
                .user(user)
                .isGood(isGood)
                .content(content)
                .build();
    }
}
