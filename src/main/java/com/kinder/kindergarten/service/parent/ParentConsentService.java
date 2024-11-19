
package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentInfoDTO;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.entity.parent.ParentConsent;
import com.kinder.kindergarten.repository.parent.ParentConsentRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)

public class ParentConsentService {

    // 학부모 동의서에 관한 서비스 로직

    private final ParentConsentRepository parentConsentRepository;

    private final ParentRepository parentRepository;

    @Transactional
    public ParentConsentDTO createConsent(ParentConsentDTO consentDTO) {
        // 새로운 동의서를 생성해주는 서비스 메서드

        if (!isAllConsented(consentDTO)) {
            throw new IllegalStateException("모든 항목에 동의해야 합니다.");
            // 모든 약관, 동의서에 동의안함이 있을 경우 메세지 출력
        }

        Parent parent = parentRepository.findById(consentDTO.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("학부모를 찾을 수 없습니다."));
        // 학부모ID를 레포지토리에서 찾아서 없을 경우 메세지 출력

        if (parentConsentRepository.existsByParentParentId(consentDTO.getParentId())) {

            throw new IllegalStateException("이미 동의서가 존재 합니다.");
        }// 학부모가 동의한 동의서, 약관이 있는지 검사

        ParentConsent consent = ParentConsent.builder()
                .termsConsent(consentDTO.getTermsConsent())
                .photoConsent(consentDTO.getPhotoConsent())
                .medicalInfoConsent(consentDTO.getMedicalInfoConsent())
                .privateConsent(consentDTO.getPrivateConsent())
                .communityConsent(consentDTO.getCommunityConsent())
                .emergencyInfoConsent(consentDTO.getEmergencyInfoConsent())
                .parent(parent)
                .build();

        ParentConsent savedConsent = parentConsentRepository.save(consent);

        return convertToDTO(savedConsent);
    }

    public boolean hasPartialConsent(Long parentId) {
        // 부분적으로 동의한 항목이 있는지 확인하는 메서드

        Optional<ParentConsent> consent = parentConsentRepository.findByParent_ParentId(parentId);
        // 학부모ID를 동의서 조회

        if (consent.isEmpty()) {
            return false;
            // 동의서가 없으면 FALSE
        }

        ParentConsent parentConsent = consent.get();
        // 하나라도 동의한 동의서가 있으면 동의서 가져오기

        return parentConsent.getTermsConsent() ||
                parentConsent.getPhotoConsent() ||
                parentConsent.getPrivateConsent() ||
                parentConsent.getMedicalInfoConsent() ||
                parentConsent.getCommunityConsent() ||
                parentConsent.getEmergencyInfoConsent();

    }

    @Transactional
    public void saveTemporaryConsent(ParentConsentDTO consentDTO) {
        try {
            ParentConsent consent = ParentConsent.builder()
                    .termsConsent(consentDTO.getTermsConsent())
                    .photoConsent(consentDTO.getPhotoConsent())
                    .medicalInfoConsent(consentDTO.getMedicalInfoConsent())
                    .privateConsent(consentDTO.getPrivateConsent())
                    .communityConsent(consentDTO.getCommunityConsent())
                    .emergencyInfoConsent(consentDTO.getEmergencyInfoConsent())
                    .build();

            parentConsentRepository.save(consent);
        } catch (Exception e) {
            log.error("임시 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("동의 정보 저장에 실패했습니다.");
        }
    }

    @Transactional
    public void saveFirstStepConsent(ParentConsentDTO consentDTO) {
        try {
            ParentConsent consent = new ParentConsent();
            consent.setTermsConsent(consentDTO.getTermsConsent());
            consent.setCommunityConsent(consentDTO.getCommunityConsent());
            consent.setPrivateConsent(consentDTO.getPrivateConsent());

            parentConsentRepository.save(consent);
        } catch (Exception e) {
            log.error("첫 번째 단계 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("동의 정보 저장에 실패했습니다.");
        }
    }

    // 두 번째 단계 동의 정보 저장 메소드 추가
    @Transactional
    public void saveSecondStepConsent(ParentConsentDTO consentDTO) {
        try {
            ParentConsent consent = new ParentConsent();
            consent.setPhotoConsent(consentDTO.getPhotoConsent());
            consent.setMedicalInfoConsent(consentDTO.getMedicalInfoConsent());
            consent.setEmergencyInfoConsent(consentDTO.getEmergencyInfoConsent());

            parentConsentRepository.save(consent);
        } catch (Exception e) {
            log.error("두 번째 단계 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("동의 정보 저장에 실패했습니다.");
        }
    }

    // 동의 상태 검증 메소드 수정
    public boolean isAllConsented(ParentConsentDTO consentDTO) {
        return (consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent()) &&
                (consentDTO.getPhotoConsent() != null && consentDTO.getPhotoConsent()) &&
                (consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent()) &&
                (consentDTO.getMedicalInfoConsent() != null && consentDTO.getMedicalInfoConsent()) &&
                (consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent()) &&
                (consentDTO.getEmergencyInfoConsent() != null && consentDTO.getEmergencyInfoConsent());
    }

    // 첫 번째 단계 동의 상태만 검증하는 메소드 추가
    public boolean isFirstStepConsented(ParentConsentDTO consentDTO) {
        return (consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent()) &&
                (consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent()) &&
                (consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent());
    }

    // 두 번째 단계 동의 상태만 검증하는 메소드 추가
    public boolean isSecondStepConsented(ParentConsentDTO consentDTO) {
        return (consentDTO.getPhotoConsent() != null && consentDTO.getPhotoConsent()) &&
                (consentDTO.getMedicalInfoConsent() != null && consentDTO.getMedicalInfoConsent()) &&
                (consentDTO.getEmergencyInfoConsent() != null && consentDTO.getEmergencyInfoConsent());
    }

    @Transactional
    public void saveParentInfoAndConsent(ParentInfoDTO parentInfoDTO, ParentConsentDTO consentDTO) {
        try {
            // Parent 엔티티 생성 및 저장
            Parent parent = Parent.builder()
                    .parentEmail(parentInfoDTO.getParentEmail())
                    .parentName(parentInfoDTO.getParentName())
                    .parentPhone(parentInfoDTO.getParentPhone())
                    .childrenEmergencyPhone(parentInfoDTO.getChildrenEmergencyPhone())
                    .parentAddress(parentInfoDTO.getParentAddress())
                    .detailAddress(parentInfoDTO.getDetailAddress())
                    .build();

            parent = parentRepository.save(parent);

            // ParentConsent 엔티티 생성 및 저장
            ParentConsent consent = ParentConsent.builder()
                    .termsConsent(consentDTO.getTermsConsent())
                    .photoConsent(consentDTO.getPhotoConsent())
                    .medicalInfoConsent(consentDTO.getMedicalInfoConsent())
                    .privateConsent(consentDTO.getPrivateConsent())
                    .communityConsent(consentDTO.getCommunityConsent())
                    .emergencyInfoConsent(consentDTO.getEmergencyInfoConsent())
                    .parent(parent)
                    .build();

            parentConsentRepository.save(consent);
        } catch (Exception e) {
            log.error("학부모 정보 및 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("학부모 정보 저장에 실패했습니다.", e);
        }
    }

    public boolean isEmailDuplicate(String email) {
        return parentRepository.existsByParentEmail(email);
    }

    private ParentConsentDTO convertToDTO(ParentConsent consent) {
        // ParentConsent Entity를 DTO로 변환하는 메서드

        return ParentConsentDTO.builder()
                .parentConsentId(consent.getParentConsentId())
                .termsConsent(consent.getTermsConsent())
                .photoConsent(consent.getPhotoConsent())
                .medicalInfoConsent(consent.getMedicalInfoConsent())
                .emergencyInfoConsent(consent.getEmergencyInfoConsent())
                .privateConsent(consent.getPrivateConsent())
                .communityConsent(consent.getCommunityConsent())
                .parentId(consent.getParentConsentId())
                .build();
    }
}
