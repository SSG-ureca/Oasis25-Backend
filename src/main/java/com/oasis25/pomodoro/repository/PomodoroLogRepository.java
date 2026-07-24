package com.oasis25.pomodoro.repository;

import com.oasis25.pomodoro.entity.PomodoroLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PomodoroLogRepository extends JpaRepository<PomodoroLog, Long> {

        @Query("select p from PomodoroLog p where p.user.id = :userId and p.createdAt between :start and :end order by p.createdAt desc")
        List<PomodoroLog> findByUserIdAndCreatedAtBetween(
                        @Param("userId") Long userId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("select p from PomodoroLog p where p.id = :id and p.user.id = :userId")
        Optional<PomodoroLog> findByIdAndUserId(
                        @Param("id") Long id,
                        @Param("userId") Long userId);

        @Query("select p from PomodoroLog p where p.user.id = :userId and p.completed = true " +
                        "and p.createdAt >= :start and p.createdAt < :end order by p.createdAt asc")
        List<PomodoroLog> findCompletedByUserIdAndCreatedAtBetween(
                        @Param("userId") Long userId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("select p from PomodoroLog p where p.user.id = :userId and p.completed = true " +
                        "and p.elapsedFocusSeconds > 0 order by p.createdAt asc")
        List<PomodoroLog> findCompletedByUserId(
                        @Param("userId") Long userId);

        @Query("select p.weatherCondition as weatherCondition, " +
                        "avg(p.elapsedFocusSeconds) as avgElapsedSeconds " +
                        "from PomodoroLog p where p.user.id = :userId and p.completed = true " +
                        "and p.weatherCondition is not null " +
                        "and p.createdAt >= :start and p.createdAt < :end " +
                        "group by p.weatherCondition")
        List<WeatherStatsProjection> findWeatherStatsByUserIdAndCreatedAtBetween(
                        @Param("userId") Long userId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);
}
