package com.oasis25.pomodoro.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.pomodoro.dto.PomodoroPresetCreateRequest;
import com.oasis25.pomodoro.dto.PomodoroPresetResponse;
import com.oasis25.pomodoro.dto.PomodoroPresetUpdateRequest;
import com.oasis25.pomodoro.entity.PomodoroPreset;
import com.oasis25.pomodoro.repository.PomodoroPresetRepository;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PomodoroPresetService {

    private static final int MAX_CUSTOM_PRESETS = 2;
    private static final int DEFAULT_FOCUS_MINUTES = 25;
    private static final int DEFAULT_BREAK_MINUTES = 5;

    private final PomodoroPresetRepository pomodoroPresetRepository;
    private final UserRepository userRepository;

    @Transactional
    public PomodoroPresetResponse create(PomodoroPresetCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (pomodoroPresetRepository.countByUserIdAndIsDefaultFalse(userId) >= MAX_CUSTOM_PRESETS) {
            throw new CustomException(ErrorCode.PRESET_LIMIT_EXCEEDED);
        }
        User user = userRepository.getReferenceById(userId);
        PomodoroPreset preset = PomodoroPreset.create(user, request.getName(), request.getFocusMinutes(), request.getBreakMinutes(), false);
        pomodoroPresetRepository.save(preset);
        return toResponse(preset);
    }

    public List<PomodoroPresetResponse> findAll() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<PomodoroPreset> presets = pomodoroPresetRepository.findByUserIdOrderByDefaultDescCreatedAtAsc(userId);
        if (presets.isEmpty()) {
            User user = userRepository.getReferenceById(userId);
            PomodoroPreset defaultPreset = PomodoroPreset.create(user, "기본", DEFAULT_FOCUS_MINUTES, DEFAULT_BREAK_MINUTES, true);
            pomodoroPresetRepository.save(defaultPreset);
            presets = List.of(defaultPreset);
        }
        return presets.stream().map(this::toResponse).toList();
    }

    @Transactional
    public PomodoroPresetResponse update(Long id, PomodoroPresetUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        PomodoroPreset preset = pomodoroPresetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        preset.update(request.getName(), request.getFocusMinutes(), request.getBreakMinutes());
        return toResponse(preset);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        PomodoroPreset preset = pomodoroPresetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (preset.isDefault()) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_DEFAULT_PRESET);
        }
        pomodoroPresetRepository.delete(preset);
    }

    private PomodoroPresetResponse toResponse(PomodoroPreset preset) {
        return new PomodoroPresetResponse(
                preset.getId(),
                preset.getName(),
                preset.getFocusMinutes(),
                preset.getBreakMinutes(),
                preset.isDefault(),
                preset.getCreatedAt()
        );
    }
}
