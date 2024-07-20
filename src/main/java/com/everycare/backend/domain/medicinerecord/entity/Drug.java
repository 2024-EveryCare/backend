package com.everycare.backend.domain.medicinerecord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql  = "UPDATE drug SET is_deleted = true WHERE drug_id = ?")
@Where(clause = "is_deleted IS NULL")
@Table(name = "drug")
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_id", unique = true, nullable = false)
    private Long id;

    private String name;


    @Enumerated(EnumType.STRING)
    private Etc_Otc etc_otc;


    @ManyToMany(mappedBy = "drugs")
    private List<MedicineRecord> medicineRecords = new ArrayList<>();

    @Column(name = "is_deleted")
    private Boolean isDeleted;


}
