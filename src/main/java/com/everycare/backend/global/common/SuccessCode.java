package com.everycare.backend.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // member
    MEMBER_SIGNUP_SUCCESS(HttpStatus.CREATED, "M001", "회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK,"M002" ,"로그인 성공")

    ;

    private HttpStatus status;
    private String code;
    private String message;
}
