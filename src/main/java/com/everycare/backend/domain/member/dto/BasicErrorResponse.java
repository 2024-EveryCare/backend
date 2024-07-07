package com.everycare.backend.domain.member.dto;

public class BasicErrorResponse {
    private String code;
    private String status;
    private String message;

    public BasicErrorResponse(String code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    // getter and setter
}
