package com.everycare.backend.domain.medicinerecord.controller;

import com.everycare.backend.domain.flaskocr.controller.UploadController;
import com.everycare.backend.domain.medicinerecord.dto.*;
import com.everycare.backend.domain.medicinerecord.service.MedicineRecordService;
import com.everycare.backend.domain.member.dto.CustomUserDetails;
import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.everycare.backend.global.common.SuccessCode.*;

@RestController
@Tag(name = "MedicineRecord API", description = "복용내역 등록, 조회, 삭제 API")
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineRecordController {

    @Autowired
    private MedicineRecordService medicineRecordService;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private Long getAuthenticatedMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getMemberId();
        }
        return null;
    }


    @PostMapping(value = "/direct-records/{memberId}", produces = "application/json")
    @Operation(summary = "직접 복용내역 입력 API", description = "OCR없이 직접 복용 내역을 등록합니다.")
    public ResponseEntity<RestApiResponse> createRecord(@RequestBody MedicineRecordRequest request) {

        Long memberId = getAuthenticatedMemberId();
        if (memberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
        medicineRecordService.saveRecord(memberId, request);
            return ResponseEntity.ok(RestApiResponse.of(MEDICINE_RECORD_SUCCESS));
    }

    @GetMapping(value = "/records/{memberId}/{date}", produces = "application/json")
    @Operation(summary = "사용자 복용내역 조회 API", description = "사용자가 입력한 날짜에 해당하는 복용내역을 조회한다.")
    public ResponseEntity<RestApiResponse> getMedicineRecordsForMonth( @PathVariable String date) {
        Long memberId = getAuthenticatedMemberId();
        if (memberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }

        LocalDate localDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<MonthlyMedicineRecordResponse> records = medicineRecordService.getMedicineRecordsForMonth(memberId, localDate);
        return ResponseEntity.ok(RestApiResponse.of(FIND_MEDICINE_RECORD_SUCCESS, records));
    }

    @DeleteMapping(value = "/records", produces = "application/json")
    @Operation(summary = "복용내역 삭제 API", description = "사용자의 특정 복용내역을 삭제합니다.")
    public ResponseEntity<RestApiResponse> deleteRecord(@RequestBody DeleteRecordRequest request) {
        Long memberId = getAuthenticatedMemberId();
        if (memberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }
        medicineRecordService.deleteRecord(memberId, request.getDrugName(), request.getIntakeStart(), request.getIntakeEnd());
        return ResponseEntity.ok(RestApiResponse.of(MEDICINE_RECORD_DELETE_SUCCESS));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleDrugNotFoundException(BusinessException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(RestApiResponse.of(ex.getErrorCode()));
    }
}