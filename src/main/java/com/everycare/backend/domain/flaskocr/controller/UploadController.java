package com.everycare.backend.domain.flaskocr.controller;

import com.everycare.backend.domain.member.dto.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//약봉투 등록하기 누르면 등록 페이지로 연결해주는 컨트롤러
@Controller
@RequestMapping("/api/v1/medicines")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

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
        } else {
            // 적절한 예외 처리 또는 기본 동작 수행
            logger.error("User details not found");
            throw new IllegalStateException("User details not found");
        }

        return "upload.html"; // "upload"는 upload.html 파일을 가리킵니다.
    }
}
