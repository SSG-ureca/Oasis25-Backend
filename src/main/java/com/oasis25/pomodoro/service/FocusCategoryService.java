package com.oasis25.pomodoro.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.pomodoro.dto.FocusCategoryCreateRequest;
import com.oasis25.pomodoro.dto.FocusCategoryResponse;
import com.oasis25.pomodoro.entity.FocusCategory;
import com.oasis25.pomodoro.repository.FocusCategoryRepository;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FocusCategoryService {

    private final FocusCategoryRepository focusCategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public FocusCategoryResponse create(FocusCategoryCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (focusCategoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }
        User user = userRepository.getReferenceById(userId);
        FocusCategory category = FocusCategory.create(user, request.getName(), request.getColor());
        focusCategoryRepository.save(category);
        return toResponse(category);
    }

    public List<FocusCategoryResponse> findAll() {
        Long userId = SecurityUtil.getCurrentUserId();
        return focusCategoryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        FocusCategory category = focusCategoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        focusCategoryRepository.delete(category);
    }

    private FocusCategoryResponse toResponse(FocusCategory category) {
        return new FocusCategoryResponse(category.getId(), category.getName(), category.getColor(), category.getCreatedAt());
    }
}
