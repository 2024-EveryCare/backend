package com.everycare.backend.domain.member.dto;

public class SignupResponse {
    private String message;
    private boolean success;

    public SignupResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

}
