package com.oasis25.user.controller;

import com.oasis25.user.dto.MyProfileResponse;
import com.oasis25.user.dto.PasswordChangeRequest;
import com.oasis25.user.dto.ProfileUpdateRequest;
import com.oasis25.user.dto.UserResponse;
import com.oasis25.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 조회", description = "현재 사용자의 프로필을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @Operation(summary = "프로필 수정", description = "현재 사용자의 닉네임을 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<MyProfileResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 imgbb에 업로드하고 반환된 URL을 사용자 정보에 저장합니다.")
    @PatchMapping(value = "/me/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateProfileImage(
            @Parameter(description = "업로드할 프로필 이미지 파일", schema = @Schema(type = "string", format = "binary")) @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(userService.updateProfileImage(image));
    }
}
