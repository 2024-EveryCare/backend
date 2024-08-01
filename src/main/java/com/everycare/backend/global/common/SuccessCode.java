    package com.everycare.backend.global.common;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import org.springframework.http.HttpStatus;

    @Getter
    @AllArgsConstructor
    public enum SuccessCode {

        // member
        MEMBER_SIGNUP_SUCCESS(HttpStatus.CREATED, "M001", "회원가입 성공"),
        LOGIN_SUCCESS(HttpStatus.OK,"M002" ,"로그인 성공"),

        //medicine
        FIND_DRUG_SUCCESS(HttpStatus.OK, "F001", "검색한 약물 결과입니다."),
        FIND_DRUG_INFO_SUCCESS(HttpStatus.OK, "F002", "검색한 약물 부가 정보 결과입니다."),
        MEDICINE_RECORD_SUCCESS(HttpStatus.CREATED, "R001", "직접 복용 기록 입력하기 성공"),
        OCR_RESULT_SUCCESS(HttpStatus.CREATED, "R002", "OCR 결과 등록 성공"),
        FIND_MEDICINE_RECORD_SUCCESS(HttpStatus.OK, "R003", "복용 내역 조회 성공")
        ;

        private HttpStatus status;
        private String code;
        private String message;
    }
