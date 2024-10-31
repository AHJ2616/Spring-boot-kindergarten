package com.kinder.kindergarten.service.Employee;

import com.kinder.kindergarten.DTO.Employee.EducationDTO;
import com.kinder.kindergarten.entity.Education;
import com.kinder.kindergarten.entity.Employee;
import com.kinder.kindergarten.repository.Employee.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;

    // 교육이력 등록
    public void saveEducation(EducationDTO educationDTO, String certificatePath, Employee employee) {
        Education education = Education.builder()
                .employee(employee)
                .name(educationDTO.getEd_name())
                .startDate(educationDTO.getEd_start())
                .endDate(educationDTO.getEd_end())
                .certificate(certificatePath)
                .build();

        educationRepository.save(education);
    }

    // 교육이력 조회
    public List<EducationDTO> getEducationHistory(Employee employee) {
        return educationRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EducationDTO convertToDTO(Education education) {
        return EducationDTO.builder()
                .ed_id(education.getId())
                .ed_name(education.getName())
                .ed_start(education.getStartDate())
                .ed_end(education.getEndDate())
                .ed_certificate(education.getCertificate() != null ? "있음" : "없음")
                .build();
    }
}
