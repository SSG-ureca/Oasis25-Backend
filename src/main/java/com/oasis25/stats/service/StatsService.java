package com.oasis25.stats.service;

import com.oasis25.common.security.SecurityUtil;
import com.oasis25.pomodoro.repository.PomodoroLogRepository;
import com.oasis25.pomodoro.repository.WeatherStatsProjection;
import com.oasis25.stats.dto.WeatherFocusStatsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final PomodoroLogRepository pomodoroLogRepository;

    public List<WeatherFocusStatsResponse> getWeatherStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        return pomodoroLogRepository.findWeatherStatsByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private WeatherFocusStatsResponse toResponse(WeatherStatsProjection projection) {
        Long total = projection.getTotalCount();
        Long completed = projection.getCompletedCount();
        Double completionRate = total == null || total == 0 ? 0.0 : (completed.doubleValue() / total) * 100.0;
        return new WeatherFocusStatsResponse(
                projection.getWeatherCondition() != null ? projection.getWeatherCondition().name() : null,
                total,
                completed,
                completionRate,
                projection.getAvgFocusMinutes()
        );
    }
}
