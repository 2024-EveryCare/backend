package com.everycare.backend.domain.flaskocr.controller;

//html 테스트용
//import com.everycare.backend.domain.member.dto.CustomUserDetails;
//import com.everycare.backend.domain.flaskocr.service.QrCodeService;
//import com.everycare.backend.global.security.JwtTokenProvider;
//import com.google.zxing.WriterException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/api/v1/medicines")
//@SessionAttributes("userDetails")
//public class UploadController {
//
//    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
//    private final QrCodeService qrCodeService;
//    private final JwtTokenProvider jwtTokenProvider;
//    private final Path uploadDir = Paths.get("uploads");
//
//    @Autowired
//    public UploadController(QrCodeService qrCodeService, JwtTokenProvider jwtTokenProvider) {
//        this.qrCodeService = qrCodeService;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @GetMapping("/photoUpload")
//    public String showUploadPage(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
//
//        logger.debug("Principal: {}", principal);
//
//        if (principal instanceof CustomUserDetails) {
//            CustomUserDetails userDetails = (CustomUserDetails) principal;
//            Long memberId = userDetails.getMemberId();
//            model.addAttribute("member_id", memberId);
//            logger.debug("Member ID: {}", memberId);
//
//            try {
//                // JWT 토큰 생성
//                String token = jwtTokenProvider.createToken(userDetails.getUsername());
//
//                // QR 코드 링크 생성 (토큰 포함)
//                String link = "http://192.168.0.5:8080/api/v1/medicines/uploadOnlyPhotoMobile?token=" + token;
//
//                // QR 코드 생성
//                byte[] qrCodeBytes = qrCodeService.generateQrCode(link);
//                String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
//
//                // 모델에 QR 코드 추가
//                model.addAttribute("qrCode", qrCodeBase64);
//                model.addAttribute("uploadedPhotos", getUploadedPhotos(memberId));
//
//            } catch (IOException | WriterException e) {
//                logger.error("QR 코드 생성 중 오류 발생", e);
//                model.addAttribute("error", "QR 코드 생성 중 오류 발생");
//            }
//        } else {
//            logger.error("User details not found");
//            throw new IllegalStateException("User details not found");
//        }
//
//        return "upload";
//    }

import com.everycare.backend.domain.flaskocr.dto.PhotoUploadResponse;
import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.domain.flaskocr.repository.QrCodeRepository;
import com.everycare.backend.global.security.JwtTokenProvider;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medicines")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final QrCodeRepository qrCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Path uploadDir = Paths.get("uploads");

    @Autowired
    public UploadController(QrCodeRepository qrCodeRepository, JwtTokenProvider jwtTokenProvider) {
        this.qrCodeRepository = qrCodeRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(value = "/photoUpload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoUploadResponse> getPhotoUploadData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        PhotoUploadResponse response = new PhotoUploadResponse();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long memberId = userDetails.getMemberId();
            response.setMemberId(memberId);

            try {
                // JWT 토큰 생성
                String token = jwtTokenProvider.createToken(userDetails.getUsername());

                // QR 코드 링크 생성 (토큰 포함)
                String link = "http://192.168.219.100:8080/api/v1/medicines/uploadOnlyPhotoMobile?token=" + token;

                // QR 코드 생성
                byte[] qrCodeBytes = qrCodeRepository.generateQrCode(link);
                String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);

                // JSON 데이터 추가
                response.setQrCode(qrCodeBase64);
                response.setUploadedPhotos(getUploadedPhotos(memberId));

            } catch (IOException | WriterException e) {
                logger.error("QR 코드 생성 중 오류 발생", e);
                response.setMessage("QR 코드 생성 중 오류 발생");
            }
        } else {
            logger.error("User details not found");
            response.setMessage("User details not found");
        }

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/photoUpload")
//    public ResponseEntity<PhotoUploadResponse> handlePhotoUploadApi(@RequestParam("file") MultipartFile file) {
//        PhotoUploadResponse response = new PhotoUploadResponse();
//
//        try {
//            // 파일 업로드 처리
//            if (!Files.exists(uploadDir)) {
//                Files.createDirectories(uploadDir);
//            }
//
//            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
//            Path filePath = uploadDir.resolve(fileName);
//            Files.write(filePath, file.getBytes());
//
//            // JWT 토큰 생성
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Object principal = authentication.getPrincipal();
//
//            if (principal instanceof CustomUserDetails) {
//                CustomUserDetails userDetails = (CustomUserDetails) principal;
//
//                String token = jwtTokenProvider.createToken(userDetails.getUsername());
//
//                // QR 코드 링크 생성 (토큰 포함)
//                String link = "http://192.168.219.100:8080/api/v1/medicines/uploadOnlyPhotoMobile?token=" + token;
//
//                // QR 코드 생성
//                byte[] qrCodeBytes = qrCodeService.generateQrCode(link);
//                String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
//
//                // 업로드된 파일 목록 가져오기
//                List<String> uploadedPhotos = getUploadedPhotos(memberId);
//
//                // 응답에 QR 코드와 업로드된 파일 이름, 파일 목록을 포함
//                response.setQrCode(qrCodeBase64);
//                response.setUploadedPhotos(uploadedPhotos);
//                response.setMessage("Photo uploaded successfully");
//            } else {
//                logger.error("User details not found");
//                response.setMessage("User details not found");
//            }
//
//        } catch (IOException | WriterException e) {
//            logger.error("Error uploading photo or generating QR code", e);
//            response.setMessage("Error uploading photo or generating QR code");
//            return ResponseEntity.status(500).body(response);
//        }
//
//        return ResponseEntity.ok(response);
//    }

    private List<String> getUploadedPhotos(Long memberId) {
        try {
            // 사용자별 업로드 디렉토리 경로 설정
            Path userUploadDir = uploadDir.resolve(memberId.toString());

            if (!Files.exists(userUploadDir)) {
                return new ArrayList<>(); // 디렉토리가 없으면 빈 리스트 반환
            }

            return Files.list(userUploadDir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error retrieving uploaded photos for memberId: " + memberId, e);
            return new ArrayList<>();
        }
    }
}