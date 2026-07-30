package com.oasis25.common.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CloudinaryUploadService {

    private final Cloudinary cloudinary;

    public CloudinaryUploadService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            if (uploadResult == null || uploadResult.get("url") == null) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
