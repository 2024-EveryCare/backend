package com.everycare.backend.domain.medicinerecord.repository;

import com.everycare.backend.domain.medicinerecord.entity.MedicineRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRecordRepository extends JpaRepository<MedicineRecord, Long> {

}
