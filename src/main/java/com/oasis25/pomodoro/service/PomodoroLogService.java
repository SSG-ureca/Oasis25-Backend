package com.oasis25.pomodoro.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.pomodoro.dto.PomodoroHeatmapResponse;
import com.oasis25.pomodoro.dto.PomodoroLogCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroLogResponse;
import com.oasis25.pomodoro.entity.FocusCategory;
import com.oasis25.pomodoro.entity.PomodoroLog;
import com.oasis25.pomodoro.entity.WeatherCondition;
import com.oasis25.pomodoro.repository.FocusCategoryRepository;
import com.oasis25.pomodoro.repository.PomodoroLogRepository;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PomodoroLogService {

    private final PomodoroLogRepository pomodoroLogRepository;
    private final FocusCategoryRepository focusCategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public PomodoroLogResponse create(PomodoroLogCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        FocusCategory category = null;
        if (request.getCategoryId() != null) {
            category = focusCategoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        }
        WeatherCondition weatherCondition = parseWeatherCondition(request.getWeatherCondition());
        PomodoroLog log = PomodoroLog.create(user, category, request.getFocusMinutes(), request.getBreakMinutes(),
                weatherCondition, request.getTemperature());
        pomodoroLogRepository.save(log);
        return toResponse(log);
    }

    @Transactional
    public PomodoroLogResponse complete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        PomodoroLog log = pomodoroLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        log.complete();
        return toResponse(log);
    }

    public List<PomodoroLogResponse> findByDate(LocalDate date) {
        Long userId = SecurityUtil.getCurrentUserId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        return pomodoroLogRepository.findByUserIdAndCreatedAtBetween(userId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Map<Integer, List<PomodoroHeatmapResponse>> getHeatmap(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        List<PomodoroLog> logs = pomodoroLogRepository.findCompletedByUserId(user.getId());

        Map<Integer, Map<LocalDate, Integer>> grouped = new TreeMap<>();
        for (PomodoroLog log : logs) {
            LocalDate date = log.getCreatedAt().toLocalDate();
            int year = date.getYear();
            grouped.computeIfAbsent(year, k -> new TreeMap<>())
                    .merge(date, log.getFocusMinutes(), Integer::sum);
        }

        Map<Integer, List<PomodoroHeatmapResponse>> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<LocalDate, Integer>> yearEntry : grouped.entrySet()) {
            List<PomodoroHeatmapResponse> daily = yearEntry.getValue().entrySet().stream()
                    .map(entry -> new PomodoroHeatmapResponse(entry.getKey(), entry.getValue()))
                    .toList();
            result.put(yearEntry.getKey(), daily);
        }
        return result;
    }

    private WeatherCondition parseWeatherCondition(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        WeatherCondition byLabel = WeatherCondition.fromLabel(trimmed);
        if (byLabel != null) {
            return byLabel;
        }
        try {
            return WeatherCondition.valueOf(trimmed.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private PomodoroLogResponse toResponse(PomodoroLog log) {
        return new PomodoroLogResponse(
                log.getId(),
                log.getCategory() != null ? log.getCategory().getId() : null,
                log.getCategory() != null ? log.getCategory().getName() : null,
                log.getFocusMinutes(),
                log.getBreakMinutes(),
                log.isCompleted(),
                log.getEndTime(),
                log.getWeatherCondition() != null ? log.getWeatherCondition().getLabel() : null,
                log.getTemperature(),
                log.getCreatedAt());
    }
}
