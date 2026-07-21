package com.oasis25.diary.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.common.upload.ImgbbUploadService;
import com.oasis25.diary.dto.DiaryCreateRequest;
import com.oasis25.diary.dto.DiaryResponse;
import com.oasis25.diary.dto.DiaryUpdateRequest;
import com.oasis25.diary.entity.Diary;
import com.oasis25.diary.repository.DiaryRepository;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final ImgbbUploadService imgbbUploadService;

    @Transactional
    public DiaryResponse create(DiaryCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (diaryRepository.existsByUserIdAndDiaryDate(userId, request.getDiaryDate())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }
        User user = userRepository.getReferenceById(userId);
        Diary diary = Diary.create(user, request.getDiaryDate(), request.getContent(), request.getEmotionScore());
        diaryRepository.save(diary);
        return toResponse(diary);
    }

    public DiaryResponse getByDate(LocalDate diaryDate) {
        Long userId = SecurityUtil.getCurrentUserId();
        Diary diary = diaryRepository.findByUserIdAndDiaryDate(userId, diaryDate)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return toResponse(diary);
    }

    @Transactional
    public DiaryResponse update(Long id, DiaryUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Diary diary = diaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        diary.update(request.getContent(), request.getEmotionScore());
        return toResponse(diary);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Diary diary = diaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        diaryRepository.delete(diary);
    }

    @Transactional
    public DiaryResponse generateAiSummary(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Diary diary = diaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        String summary = createSummary(diary.getContent());
        diary.updateAiSummary(summary);
        return toResponse(diary);
    }

    @Transactional
    public DiaryResponse uploadAttachment(Long id, MultipartFile attachment) {
        Long userId = SecurityUtil.getCurrentUserId();
        Diary diary = diaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        String attachmentUrl = imgbbUploadService.upload(attachment);
        diary.updateAttachmentUrl(attachmentUrl);
        return toResponse(diary);
    }

    private String createSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        int limit = Math.min(100, content.length());
        return content.substring(0, limit) + (content.length() > limit ? "..." : "");
    }

    private DiaryResponse toResponse(Diary diary) {
        return new DiaryResponse(
                diary.getId(),
                diary.getDiaryDate(),
                diary.getContent(),
                diary.getAiSummary(),
                diary.getEmotionScore(),
                diary.getAttachmentUrl(),
                diary.getCreatedAt(),
                diary.getUpdatedAt());
    }
}
