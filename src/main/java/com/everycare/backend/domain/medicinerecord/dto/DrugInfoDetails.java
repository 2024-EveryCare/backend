package com.everycare.backend.domain.medicinerecord.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInfoDetails {
    private String imageUrl;
    private String name;
    private String mainIngredient;
    private String companyName;
    private String classification;
}
