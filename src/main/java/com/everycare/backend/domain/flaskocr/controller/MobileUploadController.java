package com.everycare.backend.domain.flaskocr.controller;

import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.domain.qrcode.service.QrCodeService;
import com.everycare.backend.global.security.JwtTokenProvider;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/medicines")
@SessionAttributes("userDetails")
public class MobileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final Path uploadDir = Paths.get("uploads");


    @GetMapping("/uploadOnlyPhotoMobile")
    public String showUploadOnlyPhotoPage() {
        return "uploadOnlyPhotoMobile";
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
}
