package com.everycare.backend.domain.medicinerecord.controller;

import com.everycare.backend.domain.medicinerecord.dto.MedicineOcrCorrection;
import com.everycare.backend.domain.medicinerecord.service.MedicineOCRCorrectionService;
import com.everycare.backend.domain.medicinerecord.service.MedicineRecordService;
import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.everycare.backend.global.common.SuccessCode.OCR_RESULT_SUCCESS;

@RestController
@Tag(name = "OCR Result API", description = "OCR 결과 등록 API")
@RequestMapping("/api/v1/medicines/photo")
@RequiredArgsConstructor
public class MedicineCorrectionController {
    @Autowired
    private MedicineOCRCorrectionService medicineOCRCorrectionService;

    private Long getAuthenticatedMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getMemberId();
        }
        return null;
    }

    @PostMapping(value = "/{memberId}", produces = "application/json")
    @Operation(summary = "OCR 결과 등록 API", description = "수정된 OCR 결과 복용 내역을 데이터베이스에 등록합니다.")
    public ResponseEntity<RestApiResponse> createOcrResult( @RequestBody MedicineOcrCorrection request) {
        Long memberId = getAuthenticatedMemberId();
        if (memberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
        medicineOCRCorrectionService.saveOcrResult(memberId, request);
        return ResponseEntity.ok(RestApiResponse.of(OCR_RESULT_SUCCESS));
    }
}

