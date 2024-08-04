package com.everycare.backend.domain.medicinerecord.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindDrugRequest {

        private String imageUrl;
        private String name;
}
