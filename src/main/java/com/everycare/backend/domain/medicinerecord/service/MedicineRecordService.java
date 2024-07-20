package com.everycare.backend.domain.medicinerecord.service;

import com.everycare.backend.domain.medicinerecord.dto.DrugApiResponse;
import com.everycare.backend.domain.medicinerecord.dto.MedicineRecordRequest;
import com.everycare.backend.domain.medicinerecord.entity.Drug;
import com.everycare.backend.domain.medicinerecord.entity.MedicineRecord;
import com.everycare.backend.domain.medicinerecord.repository.DrugRepository;
import com.everycare.backend.domain.medicinerecord.repository.MedicineRecordRepository;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineRecordService {

    @Autowired
    private MedicineRecordRepository medicineRecordRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService05/getDrugPrdtPrmsnInq05";
    private static final String API_KEY = "UoH7zxZQ8mjC4K7EeL6sKUOZWjEAvGdSTQfghxyDP3PvDkJVWoaPtyioZPkXdzhatkT9raSkeMhBL7uNfriksg==";


    public MedicineRecord saveRecord(Long memberId, MedicineRecordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<Drug> drugs = findOrFetchDrug(request.getDrugNames());

        MedicineRecord record = new MedicineRecord();
        record.setMember(member);
        record.setDrugs(drugs);
        record.setHospital(request.getHospital());
        record.setDisease(request.getDisease());
        record.setIntakeStart(request.getIntakeStart());
        record.setIntakeEnd(request.getIntakeEnd());
        record.setIntakeDaily(request.getIntakeDaily());
        record.setIntakeCycle(request.getIntakeCycle());
        return medicineRecordRepository.save(record);
    }

    private List<Drug> findOrFetchDrug(List<String> drugNames) {
        List<Drug> drugs = new ArrayList<>();
        for (String drugName : drugNames) {
            System.out.println(drugName);
            Optional<Drug> drugOpt = drugRepository.findByName(drugName);
            if (drugOpt.isPresent()) {
                drugs.add(drugOpt.get());
            } else {
                // API에서 의약품 정보 조회
                String apiUrl = API_URL + "?serviceKey=" + API_KEY + "&item_name=" + drugName;
                try {
                    DrugApiResponse apiResponse = restTemplate.getForObject(apiUrl, DrugApiResponse.class);
                    if (apiResponse != null) {
                        DrugApiResponse.Item item = apiResponse.getBody().getItems().getItemList().get(1);
                        Drug newDrug = new Drug();
                        newDrug.setName(item.getItemName());
                        drugs.add(drugRepository.save(newDrug));
                    } else {
                        // API에서 찾지 못했을 경우, 입력된 이름을 그대로 저장
                        Drug newDrug = new Drug();
                        newDrug.setName(drugName);
                        drugs.add(drugRepository.save(newDrug));
                    }
                } catch (Exception e) {
                    // 예외가 발생했을 경우, 입력된 이름을 그대로 저장
                    Drug newDrug = new Drug();
                    newDrug.setName(drugName);
                    drugs.add(drugRepository.save(newDrug));
                }
            }
        }
        return drugs;
        }
}

