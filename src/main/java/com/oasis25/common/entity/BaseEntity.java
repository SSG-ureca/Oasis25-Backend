package com.oasis25.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseUpdatableEntity {

    @Column(nullable = true)
    private LocalDateTime deletedAt;
}
