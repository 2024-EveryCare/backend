package com.everycare.backend.domain.medicinerecord.entity;

import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql  = "UPDATE medicinerecord SET is_deleted = true WHERE record_id = ?")
@Where(clause = "is_deleted IS NULL")
@Table(name = "medicinerecord")
public class MedicineRecord extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToMany
    @JoinTable(
            name = "MEDINCINE_RECORD_DRUG",
            joinColumns = @JoinColumn(name = "record_id"),
            inverseJoinColumns = @JoinColumn(name = "drug_id")
    )
    private List<Drug> drugs = new ArrayList<>();

    private String hospital;
    private String disease;
    private LocalDate intakeStart;
    private LocalDate intakeEnd;
    private int intakeDaily;
    private int intakeCycle;


    @Column(name = "is_deleted")
    private Boolean isDeleted;


}
