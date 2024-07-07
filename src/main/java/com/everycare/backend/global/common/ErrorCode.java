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
    ;
    private HttpStatus status;
    private String code;
    private String message;
}
