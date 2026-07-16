package com.oasis25.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseCreatedEntity {

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
