package com.everycare.backend.domain.medicinerecord.service;

import com.everycare.backend.domain.medicinerecord.dto.MedicineOcrCorrection;
import com.everycare.backend.domain.medicinerecord.entity.Drug;
import com.everycare.backend.domain.medicinerecord.entity.MedicineRecord;
import com.everycare.backend.domain.medicinerecord.repository.DrugRepository;
import com.everycare.backend.domain.medicinerecord.repository.MedicineRecordRepository;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineOCRCorrectionService {
    @Autowired
    private MedicineRecordRepository medicineRecordRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private MemberRepository memberRepository;

    public MedicineRecord saveOcrResult(Long memberId, MedicineOcrCorrection request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Drug> drugs = findOrCreateDrugs(request.getDrugNames());

        MedicineRecord record = new MedicineRecord();
        record.setMember(member);
        record.setDrugs(drugs);
        record.setHospital(request.getHospital());
        record.setDisease(request.getDisease());
        record.setIntakeStart((request.getIntakeStart()));
        record.setIntakeEnd(request.getIntakeEnd());
        record.setIntakeDaily(request.getIntakeDaily());
        record.setIntakeCycle(request.getIntakeCycle());
        return medicineRecordRepository.save(record);
    }

    private List<Drug> findOrCreateDrugs(List<String> drugNames) {
        List<Drug> drugs = new ArrayList<>();
        for (String drugName : drugNames) {
            Optional<Drug> drugOpt = drugRepository.findByName(drugName);
            if (drugOpt.isPresent()) {
                drugs.add(drugOpt.get());
            } else {
                Drug newDrug = new Drug();
                newDrug.setName(drugName);
                drugs.add(drugRepository.save(newDrug));
            }
        }
        return drugs;
    }
}
