package com.kinder.kindergarten.service.employee;

import com.kinder.kindergarten.DTO.employee.CertificateDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Certificate;
import com.kinder.kindergarten.repository.employee.CertificateRepository;
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
    public void saveCertificate(CertificateDTO certificateDTO, MultipartFile file, Member member) {
        String pdfPath = null;
        if (file != null && !file.isEmpty()) {
            pdfPath = fileService.uploadAndConvertToPdf(file, member);
        }

        Certificate certificate = Certificate.builder()
                .member(member)
                .name(certificateDTO.getCe_name())
                .issued(certificateDTO.getCe_issued())
                .expri(certificateDTO.getCe_expri())
                .path(pdfPath)
                .build();

        certificateRepository.save(certificate);
    }

    // 직원별 자격증 조회
    public List<CertificateDTO> getCertificatesByEmployee(Member member) {
        return certificateRepository.findByMember(member).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CertificateDTO convertToDTO(Certificate certificate) {
        return CertificateDTO.builder()
                .ce_id(certificate.getId())
                .ce_name(certificate.getName())
                .ce_issued(certificate.getIssued())
                .ce_expri(certificate.getExpri())
                .ce_path(certificate.getPath())
                .build();
    }

    public CertificateDTO getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자격증 정보를 찾을 수 없습니다."));

        return convertToDTO(certificate);
    }

    // 자격증 수정
    @Transactional
    public void updateCertificate(Long id, CertificateDTO certificateDTO, MultipartFile file, Member member) {
        Certificate certificate = certificateRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new RuntimeException("자격증 정보를 찾을 수 없습니다."));

        // 기본 정보 업데이트
        certificate.setName(certificateDTO.getCe_name());
        certificate.setIssued(certificateDTO.getCe_issued());
        certificate.setExpri(certificateDTO.getCe_expri());

        // 새로운 파일이 업로드된 경우
        if (file != null && !file.isEmpty()) {
            // 기존 파일이 있다면 삭제
            if (certificate.getPath() != null) {
                fileService.deleteFile(certificate.getPath());
            }
            // 새 파일 업로드
            String newPdfPath = fileService.uploadAndConvertToPdf(file, member);
            certificate.setPath(newPdfPath);
        }

        certificateRepository.save(certificate);
    }

    // 자격증 삭제
    @Transactional
    public void deleteCertificate(Long id, Member member) {
        Certificate certificate = certificateRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new RuntimeException("자격증 정보를 찾을 수 없습니다."));

        // 관련 파일이 있다면 삭제
        if (certificate.getPath() != null) {
            fileService.deleteFile(certificate.getPath());
        }

        certificateRepository.delete(certificate);
    }

    // 특정 자격증이 해당 직원의 것인지 확인
    public boolean isCertificateOwnedByEmployee(Long id, Member member) {

        return certificateRepository.findByIdAndMember(id, member).isPresent();
    }
}

