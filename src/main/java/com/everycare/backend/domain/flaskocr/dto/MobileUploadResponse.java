package com.everycare.backend.domain.flaskocr.dto;

public class MobileUploadResponse {
    private String message;

    // 기본 생성자
    public MobileUploadResponse() {}

    // 메시지를 포함한 생성자
    public MobileUploadResponse(String message) {
        this.message = message;
    }

    // 메시지 getter
    public String getMessage() {
        return message;
    }

    // 메시지 setter
    public void setMessage(String message) {
        this.message = message;
    }
}