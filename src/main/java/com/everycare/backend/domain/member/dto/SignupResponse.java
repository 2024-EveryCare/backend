package com.everycare.backend.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public SignupResponse(String code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    // getter and setter
}

