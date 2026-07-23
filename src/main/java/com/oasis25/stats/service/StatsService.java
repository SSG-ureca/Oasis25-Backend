package com.oasis25.stats.service;

import com.oasis25.common.security.SecurityUtil;
import com.oasis25.diary.entity.Diary;
import com.oasis25.diary.repository.DiaryRepository;
import com.oasis25.pomodoro.entity.PomodoroLog;
import com.oasis25.pomodoro.entity.WeatherCondition;
import com.oasis25.pomodoro.repository.PomodoroLogRepository;
import com.oasis25.pomodoro.repository.WeatherStatsProjection;
import com.oasis25.stats.dto.EmotionStatsResponse;
import com.oasis25.stats.dto.TrendStatsResponse;
import com.oasis25.stats.dto.WeatherFocusStatsResponse;
import com.oasis25.stats.dto.WeeklyLogResponse;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final PomodoroLogRepository pomodoroLogRepository;
    private final DiaryRepository diaryRepository;
    private final Clock statsClock;
    private final ZoneId statsZoneId;

    public List<WeeklyLogResponse> getWeeklyLogs() {
        Long userId = SecurityUtil.getCurrentUserId();
        TimeWindow window = lastDaysWindow(7);
        return pomodoroLogRepository.findCompletedByUserIdAndCreatedAtBetween(userId, window.start, window.end)
                .stream()
                .map(this::toWeeklyLogResponse)
                .toList();
    }

    public List<WeatherFocusStatsResponse> getWeatherStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        TimeWindow window = lastDaysWindow(30);
        return pomodoroLogRepository.findWeatherStatsByUserIdAndCreatedAtBetween(userId, window.start, window.end)
                .stream()
                .map(this::toWeatherResponse)
                .toList();
    }

    public List<EmotionStatsResponse> getEmotionStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        DateRange range = lastDaysRange(30);
        List<Diary> diaries = diaryRepository.findByUserIdAndDiaryDateBetween(userId, range.startDate, range.endDate);
        return diaries.stream()
                .map(diary -> new EmotionStatsResponse(diary.getDiaryDate(), diary.getEmotionScore()))
                .toList();
    }

    public List<TrendStatsResponse> getTrend() {
        Long userId = SecurityUtil.getCurrentUserId();
        TimeWindow window = lastDaysWindow(30);
        List<PomodoroLog> logs = pomodoroLogRepository.findCompletedByUserIdAndCreatedAtBetween(userId, window.start,
                window.end);
        Map<LocalDate, Integer> totalsByDate = new LinkedHashMap<>();
        for (LocalDate date = window.startDate; !date.isAfter(window.today); date = date.plusDays(1)) {
            totalsByDate.put(date, 0);
        }
        for (PomodoroLog log : logs) {
            LocalDate date = log.getCreatedAt().atZone(statsZoneId).toLocalDate();
            totalsByDate.merge(date, log.getFocusMinutes(), Integer::sum);
        }
        return totalsByDate.entrySet().stream()
                .map(entry -> new TrendStatsResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private WeeklyLogResponse toWeeklyLogResponse(PomodoroLog log) {
        return new WeeklyLogResponse(log.getCreatedAt(), log.getFocusMinutes());
    }

    private WeatherFocusStatsResponse toWeatherResponse(WeatherStatsProjection projection) {
        WeatherCondition condition = projection.getWeatherCondition();
        return new WeatherFocusStatsResponse(
                condition != null ? condition.getLabel() : null,
                round(projection.getAvgFocusMinutes()));
    }

    private Double round(Double value) {
        if (value == null) {
            return null;
        }
        return Math.round(value * 10.0) / 10.0;
    }

    private TimeWindow lastDaysWindow(int days) {
        ZonedDateTime now = ZonedDateTime.now(statsClock);
        LocalDate today = now.toLocalDate();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay(statsZoneId).toLocalDateTime();
        LocalDateTime end = today.plusDays(1).atStartOfDay(statsZoneId).toLocalDateTime();
        return new TimeWindow(startDate, today, start, end);
    }

    private DateRange lastDaysRange(int days) {
        LocalDate today = LocalDate.now(statsClock);
        LocalDate startDate = today.minusDays(days - 1);
        return new DateRange(startDate, today.plusDays(1));
    }

    private record TimeWindow(LocalDate startDate, LocalDate today, LocalDateTime start, LocalDateTime end) {
    }

    private record DateRange(LocalDate startDate, LocalDate endDate) {
    }
}
