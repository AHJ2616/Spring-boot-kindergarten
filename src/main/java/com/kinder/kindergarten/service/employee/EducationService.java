package com.kinder.kindergarten.service.employee;

import com.kinder.kindergarten.DTO.employee.EducationDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Education;
import com.kinder.kindergarten.repository.employee.EducationRepository;
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


    // 교육이력 등록
    public void saveEducation(EducationDTO educationDTO, MultipartFile file, Member member) {
        String pdfPath = null;
        if (file != null && !file.isEmpty()) {
            pdfPath = fileService.uploadAndConvertToPdf(file, member);
        }

        Education education = Education.builder()
                .member(member)
                .name(educationDTO.getEd_name())
                .startDate(educationDTO.getEd_start())
                .endDate(educationDTO.getEd_end())
                .certificate(pdfPath)
                .build();

        educationRepository.save(education);
    }

    // 교육이력 조회
    public List<EducationDTO> getEducationHistory(Member member) {
        return educationRepository.findByMember(member).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EducationDTO getEducationById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("교육 정보를 찾을 수 없습니다."));

        return convertToDTO(education);
    }

    // 교육이력 수정
    @Transactional
    public void updateEducation(Long id, EducationDTO educationDTO, MultipartFile file, Member member) {
        Education education = educationRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new RuntimeException("교육 정보를 찾을 수 없습니다."));

        // 기본 정보 업데이트
        education.setName(educationDTO.getEd_name());
        education.setStartDate(educationDTO.getEd_start());
        education.setEndDate(educationDTO.getEd_end());

        // 새로운 파일이 업로드된 경우
        if (file != null && !file.isEmpty()) {
            // 기존 파일이 있다면 삭제
            if (education.getCertificate() != null) {
                fileService.deleteFile(education.getCertificate());
            }
            // 새 파일 업로드
            String newPdfPath = fileService.uploadAndConvertToPdf(file, member);
            education.setCertificate(newPdfPath);
        }

        educationRepository.save(education);
    }

    // 교육이력 삭제
    @Transactional
    public void deleteEducation(Long id, Member member) {
        Education education = educationRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new RuntimeException("교육 정보를 찾을 수 없습니다."));

        // 관련 파일이 있다면 삭제
        if (education.getCertificate() != null) {
            fileService.deleteFile(education.getCertificate());
        }

        educationRepository.delete(education);
    }

    // 특정 교육이력이 해당 직원의 것인지 확인
    public boolean isEducationOwnedByEmployee(Long id, Member member) {
        return educationRepository.findByIdAndMember(id, member).isPresent();
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
}
