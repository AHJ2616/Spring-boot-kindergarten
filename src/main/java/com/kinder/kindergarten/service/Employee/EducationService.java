package com.kinder.kindergarten.service.employee;

import com.kinder.kindergarten.DTO.employee.EducationDTO;
import com.kinder.kindergarten.entity.employee.Education;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Employee_File;
import com.kinder.kindergarten.repository.employee.EducationRepository;
import com.kinder.kindergarten.repository.employee.FileRepository;
import com.kinder.kindergarten.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final FileService fileService;

    private final FileRepository fileRepository;

    // 교육이력 등록
    public void saveEducation(EducationDTO educationDTO, MultipartFile file, Employee employee) {
        String pdfPath = null;
        if (file != null && !file.isEmpty()) {
            pdfPath = fileService.uploadAndConvertToPdf(file, employee);
        }

        Education education = Education.builder()
                .employee(employee)
                .name(educationDTO.getEd_name())
                .startDate(educationDTO.getEd_start())
                .endDate(educationDTO.getEd_end())
                .certificate(pdfPath)
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
                .ed_path(education.getCertificate())
                .build();
    }

    public EducationDTO getEducationById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("교육 정보를 찾을 수 없습니다."));

        return convertToDTO(education);
    }
}
