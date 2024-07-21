package com.everycare.backend.domain.medicinerecord.controller;

import com.everycare.backend.domain.medicinerecord.dto.MedicineRecordRequest;
import com.everycare.backend.domain.medicinerecord.service.MedicineRecordService;
import com.everycare.backend.global.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.everycare.backend.global.common.SuccessCode.MEDICINE_RECORD_SUCCESS;

@RestController
@Tag(name = "MedicineRecord API", description = "복용내역 등록, 조회, 수정, 삭제 API")
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineRecordController {

    @Autowired
    private MedicineRecordService medicineRecordService;

    @PostMapping(value = "/direct-records/{memberId}", produces = "application/json")
    @Operation(summary = "직접 복용내역 입력 API", description = "OCR없이 직접 복용 내역을 등록합니다.")
    public ResponseEntity<RestApiResponse> createRecord(@PathVariable Long memberId, @RequestBody MedicineRecordRequest request) {
        medicineRecordService.saveRecord(memberId, request);
            return ResponseEntity.ok(RestApiResponse.of(MEDICINE_RECORD_SUCCESS));
    }
}
//    public ResponseEntity<RestApiResponse> createRecord(HttpSession session, @RequestBody MedicineRecordRequest request) {
//        Long memberId = (Long) session.getAttribute("memberId");
//        if (memberId == null) {
//            return ResponseEntity.status(401).body(RestApiResponse.of("Unauthorized"));
//        }
//        medicineRecordService.saveRecord(memberId, request);
//        return ResponseEntity.ok(RestApiResponse.of(MEDICINE_RECORD_SUCCESS));
//    }
