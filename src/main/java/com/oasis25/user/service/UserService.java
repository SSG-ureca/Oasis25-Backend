package com.oasis25.user.service;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import com.oasis25.common.security.SecurityUtil;
import com.oasis25.common.upload.ImgbbUploadService;
import com.oasis25.user.dto.MyProfileResponse;
import com.oasis25.user.dto.PasswordChangeRequest;
import com.oasis25.user.dto.ProfileUpdateRequest;
import com.oasis25.user.dto.UserResponse;
import com.oasis25.user.entity.User;
import com.oasis25.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ImgbbUploadService imgbbUploadService;
    private final PasswordEncoder passwordEncoder;

    public MyProfileResponse getMyProfile() {
        User user = findCurrentUser();
        return toMyProfileResponse(user);
    }

    @Transactional
    public MyProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = findCurrentUser();
        user.updateNickname(request.getNickname());
        return toMyProfileResponse(user);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = findCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public UserResponse updateProfileImage(MultipartFile image) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String profileImageUrl = imgbbUploadService.upload(image);
        user.updateProfileImageUrl(profileImageUrl);

        return toResponse(user);
    }

    private User findCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private MyProfileResponse toMyProfileResponse(User user) {
        return new MyProfileResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl());
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getProfileImageUrl());
    }
}
