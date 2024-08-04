package com.everycare.backend.domain.flaskocr.controller;

import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.domain.qrcode.service.QrCodeService;
import com.everycare.backend.global.security.JwtTokenProvider;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/medicines")
@SessionAttributes("userDetails")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final QrCodeService qrCodeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Path uploadDir = Paths.get("uploads");

    @Autowired
    public UploadController(QrCodeService qrCodeService, JwtTokenProvider jwtTokenProvider) {
        this.qrCodeService = qrCodeService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/uploadOnlyPhotoMobile")
    public String showUploadOnlyPhotoPage() {
        return "uploadOnlyPhotoMobile";
    }

    @GetMapping("/photoUpload")
    public String showUploadPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        logger.debug("Principal: {}", principal);

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long memberId = userDetails.getMemberId();
            model.addAttribute("member_id", memberId);
            logger.debug("Member ID: {}", memberId);

            try {
                // JWT 토큰 생성
                String token = jwtTokenProvider.createToken(userDetails.getUsername());

                // QR 코드 링크 생성 (토큰 포함)
                String link = "http://192.168.0.5:8080/api/v1/medicines/uploadOnlyPhotoMobile?token=" + token;

                // QR 코드 생성
                byte[] qrCodeBytes = qrCodeService.generateQrCode(link);
                String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);

                // 모델에 QR 코드 추가
                model.addAttribute("qrCode", qrCodeBase64);
                model.addAttribute("uploadedPhotos", getUploadedPhotos());

            } catch (IOException | WriterException e) {
                logger.error("QR 코드 생성 중 오류 발생", e);
                model.addAttribute("error", "QR 코드 생성 중 오류 발생");
            }
        } else {
            logger.error("User details not found");
            throw new IllegalStateException("User details not found");
        }

        return "upload";
    }

    @PostMapping("/uploadOnlyPhotoMobile")
    public String handlePhotoUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            model.addAttribute("message", "Photo uploaded successfully");
        } catch (IOException e) {
            logger.error("Error uploading photo", e);
            model.addAttribute("message", "Error uploading photo");
        }

        return "redirect:/api/v1/medicines/photoUpload";
    }

    private List<String> getUploadedPhotos() {
        try {
            if (!Files.exists(uploadDir)) {
                return new ArrayList<>();
            }

            return Files.list(uploadDir)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error retrieving uploaded photos", e);
            return new ArrayList<>();
        }

    }
}
