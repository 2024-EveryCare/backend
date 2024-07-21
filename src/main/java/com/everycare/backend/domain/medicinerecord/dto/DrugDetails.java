package com.everycare.backend.domain.medicinerecord.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugDetails {

    private String itemSeq;
    private String itemName;
    private String entpName;
    private String etcOtcCode;
    private String chart;
    private String materialName;
    private String storageMethod;
    private String validTerm;
    private String makeMaterialFlag;
    private String gbnName;
    private String totalContent;
    private String eeDocData;
    private String udDocData;
    private String nbDocData;
    private String pnDocData;
    private String mainItemIngr;
    private String ingrName;
}
