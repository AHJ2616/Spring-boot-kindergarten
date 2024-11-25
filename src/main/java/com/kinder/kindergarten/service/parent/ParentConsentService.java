
package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentInfoDTO;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.entity.parent.ParentConsent;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.parent.ParentConsentRepository;
import com.kinder.kindergarten.repository.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)

public class ParentConsentService {

    // 학부모 동의서에 관한 서비스 로직

    private final ParentConsentRepository parentConsentRepository;

    private final ParentRepository parentRepository;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;


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
        // 빌드 패턴으로 서비스, 사진, 의료, 개인정보, 커뮤니티, 비상연락망 그리고 부모 를 지정

        ParentConsent savedConsent = parentConsentRepository.save(consent);
        // 지정한 데이터를 DB에 저장한다.

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
    public Long saveFirstStepConsent(ParentConsentDTO consentDTO) {
        try {
            ParentConsent consent = ParentConsent.builder()
                    .termsConsent(consentDTO.getTermsConsent())
                    .communityConsent(consentDTO.getCommunityConsent())
                    .privateConsent(consentDTO.getPrivateConsent())
                    .photoConsent(false)
                    .medicalInfoConsent(false)
                    .emergencyInfoConsent(false)
                    .consentStep(1)
                    .build();

            ParentConsent savedConsent = parentConsentRepository.save(consent);
            log.info("1단계 동의 정보 저장 완료 - ID: {}", savedConsent.getParentConsentId());

            return savedConsent.getParentConsentId(); // ID 반환
        } catch (Exception e) {
            log.error("1단계 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("동의 정보 저장에 실패했습니다: " + e.getMessage());
        }
    }

    // 두 번째 단계 동의 정보 저장 메소드 추가
    @Transactional
    public void saveSecondStepConsent(ParentConsentDTO consentDTO) {

        try {
            // 2단계 동의서 저장
            ParentConsent consent = new ParentConsent();
            consent.setPhotoConsent(consentDTO.getPhotoConsent());
            consent.setMedicalInfoConsent(consentDTO.getMedicalInfoConsent());
            consent.setEmergencyInfoConsent(consentDTO.getEmergencyInfoConsent());
            consent.setConsentStep(2);

            parentConsentRepository.save(consent);
            log.info("2단계 동의 정보 저장 완료");

        } catch (Exception e) {
            log.error("2단계 동의 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("동의 정보 저장에 실패했습니다: " + e.getMessage());
        }
    }

    public ParentConsentDTO getConsentInfo(Long parentConsentId) {
        ParentConsent consent = parentConsentRepository.findById(parentConsentId)
                .orElse(null);

        if (consent == null) {
            return new ParentConsentDTO();
        }

        return ParentConsentDTO.builder()
                .parentConsentId(consent.getParentConsentId())
                .termsConsent(consent.getTermsConsent())
                .photoConsent(consent.getPhotoConsent())
                .medicalInfoConsent(consent.getMedicalInfoConsent())
                .communityConsent(consent.getCommunityConsent())
                .privateConsent(consent.getPrivateConsent())
                .emergencyInfoConsent(consent.getEmergencyInfoConsent())
                .build();
    }

    @Transactional
    public void saveParentInfo(ParentInfoDTO parentInfoDTO) {

        try {
            Member member = Member.builder()
                    .email(parentInfoDTO.getEmail())
                    .name(parentInfoDTO.getName())
                    .phone(parentInfoDTO.getPhone())
                    .address(parentInfoDTO.getAddress())
                    .role(Role.ROLE_PARENT)
                    .password(passwordEncoder.encode("tempPassword"))
                    .build();

            memberRepository.save(member);

            Parent parent = Parent.builder()
                    .memberEmail(member.getEmail())
                    .childrenEmergencyPhone(parentInfoDTO.getChildrenEmergencyPhone())
                    .registrationStatus(RegistrationStatus.PENDING)
                    .build();

            parentRepository.save(parent);
            log.info("학부모 정보 저장 완료");

        } catch (Exception e) {
            log.error("학부모 정보 저장 중 오류 발생: ", e);
            throw new RuntimeException("학부모 정보 저장에 실패했습니다: " + e.getMessage());
        }

    }

    private boolean isAllConsented(ParentConsent consent) {
        return consent.getTermsConsent() &&
                consent.getPhotoConsent() &&
                consent.getMedicalInfoConsent() &&
                consent.getCommunityConsent() &&
                consent.getPrivateConsent() &&
                consent.getEmergencyInfoConsent();
    }

    public boolean isAllConsented(ParentConsentDTO consentDTO) {
        // 동의 상태 검증 메소드

        return (consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent()) &&
                (consentDTO.getPhotoConsent() != null && consentDTO.getPhotoConsent()) &&
                (consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent()) &&
                (consentDTO.getMedicalInfoConsent() != null && consentDTO.getMedicalInfoConsent()) &&
                (consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent()) &&
                (consentDTO.getEmergencyInfoConsent() != null && consentDTO.getEmergencyInfoConsent());
        //서비스, 사진, 커뮤니티, 의료정보, 개인정보, 비상연락망의 동의 상태를 본다.
    }


    public boolean isFirstStepConsented(ParentConsentDTO consentDTO) {
        // 첫 번째 단계 동의 상태만 검증하는 메소드

        return (consentDTO.getTermsConsent() != null && consentDTO.getTermsConsent()) &&
                (consentDTO.getCommunityConsent() != null && consentDTO.getCommunityConsent()) &&
                (consentDTO.getPrivateConsent() != null && consentDTO.getPrivateConsent());
    }


    public boolean isSecondStepConsented(ParentConsentDTO consentDTO) {
        // 두 번째 단계 동의 상태만 검증하는 메소드

        return (consentDTO.getPhotoConsent() != null && consentDTO.getPhotoConsent()) &&
                (consentDTO.getMedicalInfoConsent() != null && consentDTO.getMedicalInfoConsent()) &&
                (consentDTO.getEmergencyInfoConsent() != null && consentDTO.getEmergencyInfoConsent());
    }

    @Transactional
    public void saveParentInfoAndConsent(ParentInfoDTO parentInfoDTO, ParentConsentDTO consentDTO) {
        try {
            log.info("Starting to save parent info and consent...");

            // email null 체크
            if (parentInfoDTO.getEmail() == null || parentInfoDTO.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
            }

            log.info("Starting to save parent info and consent with email: {}", parentInfoDTO.getEmail());

            // 이메일 중복 체크
            if (memberRepository.existsById(parentInfoDTO.getEmail())) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }

            // 필수 입력값 검증
            if (parentInfoDTO.getName() == null || parentInfoDTO.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수 입력값입니다.");
            }
            if (parentInfoDTO.getPhone() == null || parentInfoDTO.getPhone().trim().isEmpty()) {
                throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
            }
            if (parentInfoDTO.getAddress() == null || parentInfoDTO.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("주소는 필수 입력값입니다.");
            }

            // Member 엔티티 생성 및 저장
            Member member = Member.builder()
                    .email(parentInfoDTO.getEmail().trim())  // 공백 제거
                    .name(parentInfoDTO.getName().trim())
                    .phone(parentInfoDTO.getPhone().trim())
                    .address(parentInfoDTO.getAddress().trim())
                    .role(Role.ROLE_PARENT)
                    .build();

            try {
                member = memberRepository.save(member);
                log.info("Member saved successfully with email: {}", member.getEmail());
            } catch (Exception e) {
                log.error("Member 저장 중 오류 발생: ", e);
                throw new RuntimeException("회원 정보 저장에 실패했습니다.");
            }


            // Parent 엔티티 생성 및 저장
            Parent parent = Parent.builder()
                    .memberEmail(member.getEmail())
                    .childrenEmergencyPhone(parentInfoDTO.getChildrenEmergencyPhone())
                    .detailAddress(parentInfoDTO.getDetailAddress())
                    .registrationStatus(RegistrationStatus.PENDING)
                    .build();

            parent = parentRepository.save(parent);
            // 멤버, 학부모 엔티티 저장한 값을 DB에 저장.
            log.info("저장한 학부모ID와 동의 상태 : "+ parent.getParentId(), parent.getRegistrationStatus());

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

            try {
                parentConsentRepository.save(consent);
                log.info("Consent saved successfully for parent ID: {}", parent.getParentId());
            } catch (Exception e) {
                log.error("Consent 저장 중 오류 발생: ", e);
                throw new RuntimeException("동의 정보 저장에 실패했습니다: " + e.getMessage());
            }

        } catch (Exception e) {
            log.error("전체 저장 프로세스 중 오류 발생: ", e);
            throw new RuntimeException("학부모 정보 저장에 실패했습니다: " + e.getMessage());
        }
    }

    public boolean isEmailDuplicate(String email) {
        return parentRepository.existsByMemberEmail(email);
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

    public List<Parent> getPendingRegistrations() {
        // 동의서 페이지에서 학부모 정보를 등록한 학부모 대기 중인 목록 조회

        log.info("ParentConsentService.getPendingRegistrations 메서드 실행 중  = = = = = =");

        List<Parent> pendingList = parentRepository.findByRegistrationStatus(RegistrationStatus.PENDING);
        log.info("Found {} pending registrations", pendingList.size());
        pendingList.forEach(parent ->

                log.info("대기중인 학부모 ID, Email, status : " +
                        parent.getParentId(),
                        parent.getMemberEmail(),
                        parent.getRegistrationStatus())
        );
        return pendingList;

    }

    @Transactional
    public void approveRegistration(Long parentId) {
        // 학부모 승인하는 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학부모를 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        parent.setRegistrationStatus(RegistrationStatus.APPROVED);
        parent.setApprovedAt(LocalDateTime.now());
        parent.setApprovedBy("관리자");
        // 승인, 승인일시, 승인자 지정

        parentRepository.save(parent);
        // 지정한 데이터를 DB에 저장
        log.info("학부모 승인 완료 - ID : " + parentId);
    }

    @Transactional
    public void rejectRegistration(Long parentId, String reason) {
        // 학부모 반려하는 서비스 메서드

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학부모를 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        parent.setRegistrationStatus(RegistrationStatus.REJECTED);
        parent.setRejectReason(reason);
        // 반려할때 반려와 사유를 지정해주고

        parentRepository.save(parent);
        // DB에 저장

        // Member 계정도 삭제 처리
        if (member != null) {
            memberRepository.delete(member);
        }

        log.info("학부모 반려 완료, 사유: " + reason);
    }

    public Parent getParentDetails(Long parentId) {

        return parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모를 찾을 수 없습니다."));
    }
}
