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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaterCaffeineLogService {

    private final WaterCaffeineLogRepository waterCaffeineLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public WaterCaffeineLogResponse create(WaterCaffeineLogCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        WaterCaffeineLog log = WaterCaffeineLog.create(user, request.getLogType(), request.getAmount());
        waterCaffeineLogRepository.save(log);
        return toResponse(log);
    }

    public List<WaterCaffeineLogResponse> findByDate(LocalDate date) {
        Long userId = SecurityUtil.getCurrentUserId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        return waterCaffeineLogRepository.findByUserIdAndCreatedAtBetween(userId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public int getTotalByDateAndType(LocalDate date, WaterCaffeineLogType type) {
        Long userId = SecurityUtil.getCurrentUserId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        Integer total = waterCaffeineLogRepository.sumAmountByUserIdAndLogTypeAndCreatedAtBetween(userId, type, start, end);
        return total == null ? 0 : total;
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        WaterCaffeineLog log = waterCaffeineLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        waterCaffeineLogRepository.delete(log);
    }

    private WaterCaffeineLogResponse toResponse(WaterCaffeineLog log) {
        return new WaterCaffeineLogResponse(log.getId(), log.getLogType(), log.getAmount(), log.getCreatedAt());
    }
}
