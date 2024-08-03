package com.everycare.backend.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestApiResponse {

    private String code;
    private HttpStatus status;
    private String message;
    private Object data;




    public static RestApiResponse of(SuccessCode successCode){
        return new RestApiResponse(successCode, "");
    }

    public static RestApiResponse of(ErrorCode errorCode){
        return new RestApiResponse(errorCode, "");
    }

    public static RestApiResponse of(SuccessCode successCode, Object data){
        return new RestApiResponse(successCode, data);
    }

    public static RestApiResponse of(ErrorCode errorCode, Object data){
        return new RestApiResponse(errorCode, data);
    }

    public RestApiResponse(SuccessCode code, Object data){
        this.code = code.getCode();
        this.status = code.getStatus();
        this.message = code.getMessage();
        this.data = data;
    }

    public RestApiResponse(ErrorCode code, Object data){
        this.code = code.getCode();
        this.status = code.getStatus();
        this.message = code.getMessage();
        this.data = data;
    }

    public RestApiResponse(ErrorCode code){
        this.code = code.getCode();
        this.status = code.getStatus();
        this.message = code.getMessage();
    }

    public RestApiResponse(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
    public void setData(Object data) {
        this.data = data;
    }
}