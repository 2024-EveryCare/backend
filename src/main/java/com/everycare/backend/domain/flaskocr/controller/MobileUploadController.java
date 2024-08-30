package com.everycare.backend.domain.flaskocr.controller;

import com.everycare.backend.domain.member.dto.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//html 테스트용
//@Controller
//@RequestMapping("/api/v1/medicines")
//@SessionAttributes("userDetails")
//public class MobileUploadController {
//
//    private static final Logger logger = LoggerFactory.getLogger(MobileUploadController.class);
//
//    // Root directory for file uploads
//    private final Path rootUploadDir = Paths.get("uploads");
//
//    @GetMapping("/uploadOnlyPhotoMobile")
//    public String showUploadOnlyPhotoPage() {
//        return "uploadOnlyPhotoMobile";
//    }
//
//    @PostMapping("/uploadOnlyPhotoMobile")
//    public String handlePhotoUpload(@RequestParam("file") MultipartFile file, Model model) {
//        try {
//            // Get authenticated user information
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Object principal = authentication.getPrincipal();
//
//            Long memberId = null;
//            if (principal instanceof CustomUserDetails) {
//                CustomUserDetails userDetails = (CustomUserDetails) principal;
//                memberId = userDetails.getMemberId();
//            }
//
//            if (memberId == null) {
//                model.addAttribute("message", "Unauthorized: member ID not found");
//                return "redirect:/api/v1/medicines/photoUpload";
//            }
//
//            if (memberId != null) {
//                model.addAttribute("memberId", memberId);
//            }
//
//            // Create user-specific directory if it doesn't exist
//            Path userUploadDir = rootUploadDir.resolve(memberId.toString());
//            if (!Files.exists(userUploadDir)) {
//                Files.createDirectories(userUploadDir);
//            }
//
//
//            // Save the file in the user-specific directory
//            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
//            Path filePath = userUploadDir.resolve(fileName);
//            Files.write(filePath, file.getBytes());
//
//            model.addAttribute("message", "Photo uploaded successfully");
//        } catch (IOException e) {
//            logger.error("Error uploading photo", e);
//            model.addAttribute("message", "Error uploading photo");
//        }
//
//        return "redirect:/api/v1/medicines/photoUpload";
//    }
//}

import com.everycare.backend.domain.flaskocr.dto.MobileUploadResponse;

@RestController
@RequestMapping("/api/v1/medicines")
public class MobileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(MobileUploadController.class);

    // Root directory for file uploads
    private final Path rootUploadDir = Paths.get("uploads");

    // 원래 코드 - json 응답 리턴 : 아이폰 사파리에서는 json 응답 표시 불가능 함!!
//    @GetMapping(value = "/uploadOnlyPhotoMobile", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<MobileUploadResponse> showUploadOnlyPhotoPage() {
//        // API endpoint doesn't actually return HTML, so provide a JSON response
//        MobileUploadResponse response = new MobileUploadResponse();
//        return ResponseEntity.ok(response);
//    }


    //연결 확인용 - 강제로 html 응답으로 변환 - 연결 확인한 후에 위의 코드로 연동 진행하면 됩니다.
    @GetMapping("/uploadOnlyPhotoMobile")
    public ResponseEntity<String> showUploadOnlyPhotoPage() {
        MobileUploadResponse response = new MobileUploadResponse();
        response.setMessage("Photo uploaded successfully");

        // HTML 형식으로 변환
        String htmlResponse = "<html><body>" +
                "<h1>Upload Status</h1>" +
                "<table border='1'>" +
                "<tr><th>Message</th></tr>" +
                "<tr><td>" + response.getMessage() + "</td></tr>" +
                "</table>" +
                "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlResponse);
    }

    @PostMapping(value = "/uploadOnlyPhotoMobile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MobileUploadResponse> handlePhotoUpload(@RequestParam("file") MultipartFile file) {
        MobileUploadResponse response = new MobileUploadResponse();
        try {
            // Get authenticated user information
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            Long memberId = null;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                memberId = userDetails.getMemberId();
            }

            if (memberId == null) {
                response.setMessage("Unauthorized: member ID not found");
                return ResponseEntity.status(403).body(response);
            }

            // Create user-specific directory if it doesn't exist
            Path userUploadDir = rootUploadDir.resolve(memberId.toString());
            if (!Files.exists(userUploadDir)) {
                Files.createDirectories(userUploadDir);
            }

            // Save the file in the user-specific directory
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = userUploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            response.setMessage("Photo uploaded successfully");
        } catch (IOException e) {
            logger.error("Error uploading photo", e);
            response.setMessage("Error uploading photo");
            return ResponseEntity.status(500).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
