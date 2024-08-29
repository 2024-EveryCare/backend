package com.everycare.backend.domain.chatbot.service;

import com.everycare.backend.domain.chatbot.dto.MedicationStatisticsResponse;
import com.everycare.backend.domain.medicinerecord.dto.MedicineAllRecordResponse;
import com.everycare.backend.domain.medicinerecord.service.MedicineRecordService;
import com.everycare.backend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.everycare.backend.global.common.ErrorCode.MEDICINE_RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final MedicineRecordService medicineRecordService;
    private final ChatGptClient chatGPTClient;  // ChatGPT API와 통신하는 클라이언트

    public MedicationStatisticsResponse generateMedicationStatistics(Long memberId) {
        // 1. 사용자 복용 내역 가져오기
        List<MedicineAllRecordResponse> medicationHistory = medicineRecordService.getMedicineRecordsAll(memberId);

        if (medicationHistory.isEmpty()) {
            throw new BusinessException(MEDICINE_RECORD_NOT_FOUND);
        }

        // 2. 프롬프트 생성
        String prompt = createPrompt(medicationHistory);

        // 3. ChatGPT API 호출하여 통계 생성
        String gptResponse = chatGPTClient.getChatGptResponse(prompt);

        // 4. 결과를 DTO로 변환하여 반환
        return new MedicationStatisticsResponse(gptResponse);
    }

    private String createPrompt(List<MedicineAllRecordResponse> medicationHistory) {
        StringBuilder prompt = new StringBuilder();
        Map<String, Integer> medicationCountMap = new HashMap<>();

        medicationHistory.forEach(record -> {
            record.getDrugNames().forEach(drugName -> {
                int count = calculateTotalIntake(record);
                medicationCountMap.put(drugName, medicationCountMap.getOrDefault(drugName, 0) + count);
            });
        });

        prompt.append("님! 올해 복용한 의약품 내역을 종합하여 다음과 같은 통계를 제공해 드립니다:\n")
                .append("올해 복용한 의약품 통계:\n");

        medicationCountMap.forEach((drugName, count) -> {
            prompt.append(drugName).append(": 총 ").append(count).append("회\n");
        });

        prompt.append("\n추가 정보:\n")
                .append("복용 빈도 분석 : 특정 약물을 많이 복용하셨는지 확인해보세요.\n")
                .append("복용 패턴 : 특정 계절이나 시기에 복용 빈도가 증가했는지 분석해보세요.\n")
                .append("복용 목적 분석:\n");

        return prompt.toString();
    }

    private int calculateTotalIntake(MedicineAllRecordResponse record) {
        int days = record.getIntakeEnd().compareTo(record.getIntakeStart()) + 1;

        // dailyIntake는 문자열에서 '1'의 개수를 세는 것으로 계산
        String intakeDaily = record.getIntakeDaily();
        int dailyIntake = (int) intakeDaily.chars().filter(ch -> ch == '1').count();

        return days * dailyIntake;
    }

}
