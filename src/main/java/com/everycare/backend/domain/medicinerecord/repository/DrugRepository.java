package com.everycare.backend.domain.medicinerecord.repository;

import com.everycare.backend.domain.medicinerecord.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    Optional<Drug> findByName(String name);
}
