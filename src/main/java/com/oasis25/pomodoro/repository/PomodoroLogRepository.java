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

        @Query("select p.weatherCondition as weatherCondition, count(p) as totalCount, " +
                        "sum(case when p.completed = true then 1 else 0 end) as completedCount, " +
                        "avg(p.focusMinutes) as avgFocusMinutes " +
                        "from PomodoroLog p where p.user.id = :userId and p.weatherCondition is not null " +
                        "group by p.weatherCondition")
        List<WeatherStatsProjection> findWeatherStatsByUserId(@Param("userId") Long userId);
}
