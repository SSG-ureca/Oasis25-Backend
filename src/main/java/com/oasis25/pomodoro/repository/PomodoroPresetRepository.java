package com.oasis25.pomodoro.repository;

import com.oasis25.pomodoro.entity.PomodoroPreset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PomodoroPresetRepository extends JpaRepository<PomodoroPreset, Long> {

    @Query("select p from PomodoroPreset p where p.user.id = :userId order by p.isDefault desc, p.createdAt asc")
    List<PomodoroPreset> findByUserIdOrderByDefaultDescCreatedAtAsc(@Param("userId") Long userId);

    @Query("select p from PomodoroPreset p where p.id = :id and p.user.id = :userId")
    Optional<PomodoroPreset> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select count(p) from PomodoroPreset p where p.user.id = :userId and p.isDefault = false")
    long countByUserIdAndIsDefaultFalse(@Param("userId") Long userId);
}
