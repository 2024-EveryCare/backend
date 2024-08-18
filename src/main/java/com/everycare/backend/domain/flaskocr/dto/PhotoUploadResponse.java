package com.everycare.backend.domain.flaskocr.dto;

import java.util.List;

public class PhotoUploadResponse {

    private Long memberId;
    private String qrCode;
    private List<String> uploadedPhotos;
    private String message;

    // Getters and Setters

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public List<String> getUploadedPhotos() {
        return uploadedPhotos;
    }

    public void setUploadedPhotos(List<String> uploadedPhotos) {
        this.uploadedPhotos = uploadedPhotos;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}