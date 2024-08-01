package com.everycare.backend.domain.medicinerecord.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MedicineOcrCorrection {
    private List<String> drugNames;
    private String hospital;
    private String disease;
    private LocalDate intakeStart;
    private LocalDate intakeEnd;
    private String intakeDaily;
    private int intakeCycle;
}



