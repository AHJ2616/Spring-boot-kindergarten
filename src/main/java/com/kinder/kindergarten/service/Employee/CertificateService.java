package com.kinder.kindergarten.service.Employee;

import com.kinder.kindergarten.DTO.Employee.CertificateDTO;
import com.kinder.kindergarten.entity.Employee.Certificate;
import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Employee_File;
import com.kinder.kindergarten.repository.Employee.CertificateRepository;
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
public class CertificateService {

    private final CertificateRepository certificateRepository;

    private final FileService fileService;

    // 자격증 등록
    public void saveCertificate(CertificateDTO certificateDTO, MultipartFile file, Employee employee) {
        if (file != null && !file.isEmpty()) {
            fileService.uploadAndConvertToPdf(file, employee);
        }

        Certificate certificate = Certificate.builder()
                .employee(employee)
                .name(certificateDTO.getCe_name())
                .issued(certificateDTO.getCe_issued())
                .expri(certificateDTO.getCe_expri())
                .build();

        certificateRepository.save(certificate);
    }

    // 직원별 자격증 조회
    public List<CertificateDTO> getCertificatesByEmployee(Employee employee) {
        return certificateRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CertificateDTO convertToDTO(Certificate certificate) {
        return CertificateDTO.builder()
                .ce_id(certificate.getId())
                .ce_name(certificate.getName())
                .ce_issued(certificate.getIssued())
                .ce_expri(certificate.getExpri())
                .build();
    }
}
