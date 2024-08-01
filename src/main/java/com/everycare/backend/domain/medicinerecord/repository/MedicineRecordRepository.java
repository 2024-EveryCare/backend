package com.everycare.backend.domain.medicinerecord.repository;

import com.everycare.backend.domain.medicinerecord.entity.MedicineRecord;
import com.everycare.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRecordRepository extends JpaRepository<MedicineRecord, Long> {
    List<MedicineRecord> findByMemberAndIntakeStartBetween(Member member, LocalDate startDate, LocalDate endDate);

}
