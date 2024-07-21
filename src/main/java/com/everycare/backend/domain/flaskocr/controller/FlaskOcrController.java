package com.everycare.backend.domain.flaskocr.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

// 인증된 세션에서 userID 가져와서 추가하는 과정 추가 예정 + Spring Security 설정(WebSecurityConfigurerAdapter) 추가 예정
@RestController
@Tag(name = "OCR 업로드 API", description = "사진 등록 API")
public class FlaskOcrController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/api/v1/medicines/photo")
    public ResponseEntity<?> uploadImageToOCR(@RequestParam("file") MultipartFile file, @RequestParam("member_id") String member_id) {
        String flaskServerUrl = "http://localhost:5000/api/v1/medicines/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Multipart 요청을 생성하기 위해 MultiValueMap을 사용
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("member_id", member_id);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(flaskServerUrl, HttpMethod.POST, requestEntity, String.class);

        System.out.println("Response JSON: " + response.getBody());

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}