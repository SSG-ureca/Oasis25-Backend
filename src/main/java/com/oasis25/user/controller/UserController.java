package com.oasis25.user.controller;

import com.oasis25.user.dto.UserResponse;
import com.oasis25.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 imgbb에 업로드하고 반환된 URL을 사용자 정보에 저장합니다.")
    @PatchMapping(value = "/me/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> updateProfileImage(
            @Parameter(description = "업로드할 프로필 이미지 파일", schema = @Schema(type = "string", format = "binary")) @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(userService.updateProfileImage(image));
    }
}
