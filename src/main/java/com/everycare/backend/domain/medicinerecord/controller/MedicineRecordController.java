package com.everycare.backend.domain.medicinerecord.controller;

import com.everycare.backend.domain.medicinerecord.dto.DrugDetails;
import com.everycare.backend.domain.medicinerecord.dto.DrugInfoDetails;
import com.everycare.backend.domain.medicinerecord.dto.MedicineRecordRequest;
import com.everycare.backend.domain.medicinerecord.service.MedicineRecordService;
import com.everycare.backend.global.common.RestApiResponse;
import com.everycare.backend.global.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.everycare.backend.global.common.SuccessCode.*;

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


    @GetMapping(value = "/findName/{drugName}", produces = "application/json")
    @Operation(summary = "의약품 검색 API", description = " '타이'를 검색하면 해당하는 단어가 전부 들어간 의약품 이름 리스트를 전부 전송")
    public ResponseEntity<RestApiResponse> getDrugNames(@PathVariable String drugName) {
        List<String> drugNames =  medicineRecordService.findDrugNames(drugName);
        return ResponseEntity.ok(RestApiResponse.of(FIND_DRUG_SUCCESS, drugNames));
    }

    @GetMapping(value = "/find-drug-info/{drugName}", produces = "application/json")
    @Operation(summary = "의약품 검색 (부가 설명 포함) API", description = " '타이'를 검색하면 해당하는 단어가 전부 들어간 의약품 정보 리스트(사진, 이름, 주성분, 회사, 구분)를 전부 전송")
    public ResponseEntity<RestApiResponse> getDrugInfos(@PathVariable String drugName) {
        List<DrugInfoDetails> drugInfos =  medicineRecordService.findDrugInfos(drugName);
        return ResponseEntity.ok(RestApiResponse.of(FIND_DRUG_INFO_SUCCESS, drugInfos));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleDrugNotFoundException(BusinessException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(RestApiResponse.of(ex.getErrorCode()));
    }

    @GetMapping(value = "/{drugName}", produces = "application/json")
    @Operation(summary = "의약품 상세정보 조회 API", description = "사용자가 선택한 의약품의 상세정보 조회 API")
    public DrugDetails getDrugDetails(@PathVariable String drugName) {
        return medicineRecordService.findDrugDetailsByName(drugName);
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
