package com.oasis25.water.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import com.oasis25.water.dto.WaterCaffeineLogCreateRequest;
import com.oasis25.water.dto.WaterCaffeineLogResponse;
import com.oasis25.water.entity.WaterCaffeineLog;
import com.oasis25.water.entity.WaterCaffeineLogType;
import com.oasis25.water.repository.WaterCaffeineLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaterCaffeineLogService {

    private final WaterCaffeineLogRepository waterCaffeineLogRepository;
    private final UserRepository userRepository;
    private final ZoneId statsZoneId;

    @Transactional
    public WaterCaffeineLogResponse create(WaterCaffeineLogCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        WaterCaffeineLog waterCaffeineLog = WaterCaffeineLog.create(user, request.getLogType(), request.getAmount());
        waterCaffeineLogRepository.save(waterCaffeineLog);
        WaterCaffeineLogResponse response = toResponse(waterCaffeineLog);
        log.info("[WaterCaffeine] create userId={} response={}", userId, response);
        return response;
    }

    public List<WaterCaffeineLogResponse> findByDate(LocalDate date) {
        Long userId = SecurityUtil.getCurrentUserId();
        ZonedDateTime startZdt = date.atStartOfDay(statsZoneId);
        LocalDateTime start = startZdt.toLocalDateTime();
        LocalDateTime end = startZdt.plusDays(1).minusNanos(1).toLocalDateTime();
        List<WaterCaffeineLogResponse> result = waterCaffeineLogRepository
                .findByUserIdAndCreatedAtBetween(userId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
        log.info("[WaterCaffeine] findByDate userId={} date={} range=[{} ~ {}] resultCount={}", userId, date, start,
                end, result.size());
        return result;
    }

    public int getTotalByDateAndType(LocalDate date, WaterCaffeineLogType type) {
        Long userId = SecurityUtil.getCurrentUserId();
        ZonedDateTime startZdt = date.atStartOfDay(statsZoneId);
        LocalDateTime start = startZdt.toLocalDateTime();
        LocalDateTime end = startZdt.plusDays(1).minusNanos(1).toLocalDateTime();
        Integer total = waterCaffeineLogRepository.sumAmountByUserIdAndLogTypeAndCreatedAtBetween(userId, type, start,
                end);
        log.info("[WaterCaffeine] getTotal userId={} date={} type={} total={}", userId, date, type, total);
        return total == null ? 0 : total;
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        WaterCaffeineLog waterCaffeineLog = waterCaffeineLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        waterCaffeineLogRepository.delete(waterCaffeineLog);
    }

    private WaterCaffeineLogResponse toResponse(WaterCaffeineLog log) {
        return new WaterCaffeineLogResponse(log.getId(), log.getLogType(), log.getAmount(), log.getCreatedAt());
    }
}
