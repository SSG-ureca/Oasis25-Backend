package com.oasis25.feedback.service;

import com.oasis25.common.security.SecurityUtil;
import com.oasis25.feedback.dto.FeedbackCreateRequest;
import com.oasis25.feedback.dto.FeedbackResponse;
import com.oasis25.feedback.entity.Feedback;
import com.oasis25.feedback.repository.FeedbackRepository;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedbackResponse create(FeedbackCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        Feedback feedback = Feedback.create(user, request.getIsGood(), request.getContent());
        feedbackRepository.save(feedback);
        return toResponse(feedback);
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(feedback.getId(), feedback.isGood(), feedback.getContent(), feedback.getCreatedAt());
    }
}
