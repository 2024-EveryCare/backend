package com.everycare.backend.domain.medicinerecord.service;

import com.everycare.backend.domain.medicinerecord.dto.*;
import com.everycare.backend.domain.medicinerecord.entity.Drug;
import com.everycare.backend.domain.medicinerecord.entity.MedicineRecord;
import com.everycare.backend.domain.medicinerecord.repository.DrugRepository;
import com.everycare.backend.domain.medicinerecord.repository.MedicineRecordRepository;
import com.everycare.backend.domain.member.entity.Member;
import com.everycare.backend.domain.member.repository.MemberRepository;
import com.everycare.backend.global.common.ErrorCode;
import com.everycare.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private static final String DRUG_INFO_API_URL = "http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService05/getDrugPrdtPrmsnDtlInq04";
    private static final String API_KEY = "UoH7zxZQ8mjC4K7EeL6sKUOZWjEAvGdSTQfghxyDP3PvDkJVWoaPtyioZPkXdzhatkT9raSkeMhBL7uNfriksg==";

    public MedicineRecord saveRecord(Long memberId, MedicineRecordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

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

    public List<FindDrugRequest> findDrugNames(String drugName) {
        List<FindDrugRequest> result = new ArrayList<>();
        Optional<Drug> drugOpt = drugRepository.findByName(drugName);
        if (drugOpt.isPresent()) {
            FindDrugRequest details = new FindDrugRequest();
            details.setName(drugOpt.get().getName());
            result.add(details);
        } else {
            // API에서 의약품 정보 조회
            String apiUrl = API_URL + "?serviceKey=" + API_KEY + "&type=json" + "&item_name=" + drugName;
            try {
                DrugApiResponse apiResponse = restTemplate.getForObject(apiUrl, DrugApiResponse.class);
                if (apiResponse != null && apiResponse.getBody() != null && apiResponse.getBody().getItems() != null) {
                    List<DrugApiResponse.Item> itemList = apiResponse.getBody().getItems();
                    result.addAll(itemList.stream().map(item -> {
                        FindDrugRequest details = new FindDrugRequest();
                        details.setName(item.getItemName());
                        details.setImageUrl(item.getBigPrdtImgUrl());
                        return details;
                    }).collect(Collectors.toList()));
                }
            } catch (Exception e) {
                // 예외 발생 시 무시
            }
            if (result.isEmpty()) {
                throw new BusinessException(ErrorCode.DRUG_NOT_FOUND);
            }
        }
        return result;
    }

    public List<DrugInfoDetails> findDrugInfos(String drugName) {
        String apiUrl = API_URL + "?serviceKey=" + API_KEY + "&type=json" + "&item_name=" + drugName;
        List<DrugInfoDetails> result = new ArrayList<>();

        try {
            DrugApiResponse apiResponse = restTemplate.getForObject(apiUrl, DrugApiResponse.class);
            if (apiResponse != null && apiResponse.getBody() != null && apiResponse.getBody().getItems() != null) {
                List<DrugApiResponse.Item> itemList = apiResponse.getBody().getItems();
                for (DrugApiResponse.Item item : itemList) {
                    DrugInfoDetails details = new DrugInfoDetails();
                    details.setImageUrl(item.getBigPrdtImgUrl());
                    details.setName(item.getItemName());
                    details.setMainIngredient(item.getItemIngrName());
                    details.setCompanyName(item.getEntpName());
                    details.setClassification(item.getSpcltyPblc());
                    result.add(details);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.DRUG_NOT_FOUND);
        }
        return result;
    }


    public DrugDetails findDrugDetailsByName(String name) {
        String apiUrl = DRUG_INFO_API_URL + "?serviceKey=" + API_KEY + "&type=json" + "&item_name=" + name;
        try {
            DetailedDrugApiResponse apiResponse = restTemplate.getForObject(apiUrl, DetailedDrugApiResponse.class);
            if (apiResponse != null && apiResponse.getBody() != null && apiResponse.getBody().getItems() != null) {
                DetailedDrugApiResponse.Item item = apiResponse.getBody().getItems().get(0);
                if (item != null) {
                    return new DrugDetails(
                            item.getItemSeq(),
                            item.getItemName(),
                            item.getEntpName(),
                            item.getEtcOtcCode(),
                            item.getChart(),
                            item.getMaterialName(),
                            item.getStorageMethod(),
                            item.getValidTerm(),
                            item.getMakeMaterialFlag(),
                            item.getGbnName(),
                            item.getTotalContent(),
                            item.getEeDocData(),
                            item.getUdDocData(),
                            item.getNbDocData(),
                            item.getPnDocData(),
                            item.getMainItemIngr(),
                            item.getIngrName()
                    );
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 무시
            e.printStackTrace();
        }
        return null;
    }

    public List<MedicineRecordResponse> getMedicineRecordsForMonth(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<MedicineRecord> records = medicineRecordRepository.findByMember(member);

        List<MedicineRecord> filteredRecords = records.stream()
                .filter(record -> !date.isBefore(record.getIntakeStart()) && !date.isAfter(record.getIntakeEnd()))
                .collect(Collectors.toList());


        // 복용내역을 응답 형태로 변환합니다.
        return filteredRecords.stream()
                .map(record -> {
                    MedicineRecordResponse recordResponse = new MedicineRecordResponse();
                    recordResponse.setDrugNames(record.getDrugs().stream().map(Drug::getName).collect(Collectors.toList()));
                    recordResponse.setIntakeDaily(record.getIntakeDaily());
                    recordResponse.setIntakeStart(record.getIntakeStart());
                    recordResponse.setIntakeEnd(record.getIntakeEnd());
                    return recordResponse;
                })
                .collect(Collectors.toList());
    }


    public List<MedicineAllRecordResponse> getMedicineRecordsAll(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<MedicineRecord> records = medicineRecordRepository.findByMember(member);

        return records.stream()
                .map(record -> {
                    MedicineAllRecordResponse response = new MedicineAllRecordResponse();
                    response.setDrugNames(record.getDrugs().stream().map(Drug::getName).collect(Collectors.toList()));
                    response.setIntakeStart(record.getIntakeStart());
                    response.setIntakeEnd(record.getIntakeEnd());
                    response.setIntakeDaily(record.getIntakeDaily());
                    return response;
                }).collect(Collectors.toList());
    }

    public void deleteRecord(Long memberId, String drugName, LocalDate intakeStart, LocalDate intakeEnd) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        List<MedicineRecord> records = medicineRecordRepository.findByMemberAndIntakeStartAndIntakeEnd(member, intakeStart, intakeEnd);
        MedicineRecord recordToDeleteDrugFrom = records.stream()
                .filter(record -> record.getDrugs().stream().anyMatch(drug -> drug.getName().equals(drugName)))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_RECORD_NOT_FOUND));

        Drug drugToDelete = recordToDeleteDrugFrom.getDrugs().stream()
                .filter(drug -> drug.getName().equals(drugName))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DRUG_NOT_FOUND));

        recordToDeleteDrugFrom.getDrugs().remove(drugToDelete);
        medicineRecordRepository.save(recordToDeleteDrugFrom);
    }


}

