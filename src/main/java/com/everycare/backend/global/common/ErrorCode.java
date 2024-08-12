package com.everycare.backend.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // member
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "E001", "이메일이 이미 존재합니다."),
    EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "E002", "회원 정보가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"E003" ,"비밀번호가 올바르지 않습니다." ),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST,"E004", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST,"E005", "비밀번호 형식이 올바르지 않습니다."),
    LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, "E006", "로그인 실패"),
    LOGOUT_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR,"E007", "로그아웃 실패"),

    // drug
    DRUG_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "일치하는 의약품 항목이 없습니다."),

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "E007", "사용자가 인증되지 않았습니다."),
  
    // medicine record
    MEDICINE_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "해당 복용 내역을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "E009", "회원 정보를 찾을 수 없습니다."),

    //generic
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "내부 서버 오류")
    ;
    private HttpStatus status;
    private String code;
    private String message;
}
