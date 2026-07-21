package com.oasis25.common.upload;

import com.oasis25.common.exception.CustomException;
import com.oasis25.common.exception.ErrorCode;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImgbbUploadService {

    private static final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${imgbb.api-key}")
    private String apiKey;

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = IMGBB_UPLOAD_URL + "?key=" + apiKey;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);
            if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data == null || data.get("url") == null) {
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
            return (String) data.get("url");
        } catch (RestClientException | java.io.IOException e) {
            log.error("imgbb upload failed", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
