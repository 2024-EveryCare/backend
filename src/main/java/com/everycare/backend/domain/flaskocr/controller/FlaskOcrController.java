package com.everycare.backend.domain.flaskocr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

@RestController
@Tag(name = "OCR 업로드 API", description = "사진 등록 API")
public class FlaskOcrController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/api/v1/medicines/photo")
    public ResponseEntity<?> uploadImageToOCR(@RequestParam("file") MultipartFile file, @RequestParam("member_id") String member_id) throws IOException {
        String flaskServerUrl = "http://flask-server:5000/api/v1/medicines/upload";     // 도커용
        // String flaskServerUrl = "http://localhost:5000/api/v1/medicines/upload";     // 로컬용

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));  // JSON 응답을 받기 위해 Accept 헤더 추가

        // Multipart 요청을 생성하기 위해 MultiValueMap을 사용
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 임시 파일 생성
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        convFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        body.add("file", new FileSystemResource(convFile));
        body.add("member_id", member_id);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(flaskServerUrl, HttpMethod.POST, requestEntity, Map.class);

        System.out.println("Response JSON: " + response.getBody());

        // 임시 파일 삭제
        if (!convFile.delete()) {
            System.err.println("Failed to delete temporary file: " + convFile.getAbsolutePath());
        }

        // Map을 JSON 문자열로 변환
        String jsonResponse = objectMapper.writeValueAsString(response.getBody());

        return ResponseEntity.status(response.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }
}
