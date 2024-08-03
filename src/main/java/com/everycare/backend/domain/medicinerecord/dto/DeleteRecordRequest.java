package com.everycare.backend.domain.medicinerecord.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DeleteRecordRequest {
    private String drugName;
    private LocalDate intakeStart;
    private LocalDate intakeEnd;


}
