package com.oasis25.water.repository;

import com.oasis25.water.entity.WaterCaffeineLog;
import com.oasis25.water.entity.WaterCaffeineLogType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaterCaffeineLogRepository extends JpaRepository<WaterCaffeineLog, Long> {

    @Query("select w from WaterCaffeineLog w where w.user.id = :userId and w.createdAt between :start and :end order by w.createdAt desc")
    List<WaterCaffeineLog> findByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("select w from WaterCaffeineLog w where w.id = :id and w.user.id = :userId")
    Optional<WaterCaffeineLog> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId);

    @Query("select coalesce(sum(w.amount), 0) from WaterCaffeineLog w where w.user.id = :userId and w.logType = :logType and w.createdAt between :start and :end")
    Integer sumAmountByUserIdAndLogTypeAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("logType") WaterCaffeineLogType logType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
