
package com.kinder.kindergarten.service.parent;

import com.kinder.kindergarten.DTO.parent.ParentConsentDTO;
import com.kinder.kindergarten.DTO.parent.ParentConsentDetailDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // 대기 중인 학부모 목록 조회 (페이징 추가)
    public Page<Parent> getPendingRegistrations(Pageable pageable) {
        log.info("대기 중인 학부모 목록 조회 시작");
        try {
            Page<Parent> pendingList = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.PENDING,
                    pageable
            );
            log.info("대기 중인 학부모 수: {}", pendingList.getTotalElements());

            pendingList.forEach(parent ->
                    log.info("대기 중인 학부모 - ID: {}, Email: {}, Status: {}",
                            parent.getParentId(),
                            parent.getMemberEmail(),
                            parent.getRegistrationStatus())
            );

            return pendingList;
        } catch (Exception e) {
            log.error("대기 중인 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 승인된 학부모 목록 조회 (페이징 추가)
    public Page<Parent> getApprovedRegistrations(Pageable pageable) {
        log.info("승인된 학부모 목록 조회 시작");
        try {
            Page<Parent> approvedList = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.APPROVED,
                    pageable
            );
            log.info("승인된 학부모 수: {}", approvedList.getTotalElements());
            return approvedList;
        } catch (Exception e) {
            log.error("승인된 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 반려된 학부모 목록 조회 (페이징 추가)
    public Page<Parent> getRejectedRegistrations(Pageable pageable) {
        log.info("반려된 학부모 목록 조회 시작");
        try {
            Page<Parent> rejectedList = parentRepository.findByRegistrationStatus(
                    RegistrationStatus.REJECTED,
                    pageable
            );
            log.info("반려된 학부모 수: {}", rejectedList.getTotalElements());
            return rejectedList;
        } catch (Exception e) {
            log.error("반려된 학부모 목록 조회 중 오류 발생: ", e);
            throw new RuntimeException("목록 조회 중 오류가 발생했습니다.");
        }
    }

    // 승인 처리 메서드 보완
    @Transactional
    public void approveRegistration(Long parentId) {
        try {
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 학부모를 찾을 수 없습니다."));

            // 이미 처리된 요청인지 확인
            if (parent.getRegistrationStatus() != RegistrationStatus.PENDING) {
                throw new IllegalStateException("이미 처리된 요청입니다.");
            }

            parent.setRegistrationStatus(RegistrationStatus.APPROVED);
            parent.setApprovedAt(LocalDateTime.now());

            parentRepository.save(parent);
            log.info("학부모 승인 완료 - ID: {}, 승인일시: {}",
                    parentId, parent.getApprovedAt());

        } catch (Exception e) {
            log.error("학부모 승인 처리 중 오류 발생: ", e);
            throw new RuntimeException("승인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 반려 처리 메서드 보완
    @Transactional
    public void rejectRegistration(Long parentId, String reason) {
        try {
            Parent parent = parentRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 학부모를 찾을 수 없습니다."));

            // 이미 처리된 요청인지 확인
            if (parent.getRegistrationStatus() != RegistrationStatus.PENDING) {
                throw new IllegalStateException("이미 처리된 요청입니다.");
            }

            // 반려 사유 검증
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("반려 사유를 입력해주세요.");
            }

            parent.setRegistrationStatus(RegistrationStatus.REJECTED);
            parent.setRejectReason(reason);
            parent.setApprovedAt(LocalDateTime.now());

            parentRepository.save(parent);
            log.info("학부모 반려 완료 - ID: {}, 사유: {}, 처리일시: {}",
                    parentId, reason, parent.getApprovedAt());

            // Member 계정도 삭제 처리
            Member member = memberRepository.findByEmail(parent.getMemberEmail());
            if (member != null) {
                memberRepository.delete(member);
                log.info("회원 정보 삭제 완료 - Email: {}", member.getEmail());
            }

        } catch (Exception e) {
            log.error("학부모 반려 처리 중 오류 발생: ", e);
            throw new RuntimeException("반려 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public Parent getParentDetails(Long parentId) {

        return parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("학부모를 찾을 수 없습니다."));
    }

    public String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        return name.substring(0, 1) + "*".repeat(name.length() - 2) +
                name.substring(name.length() - 1);
    }

    public String maskEmail(String email) {
        if (email == null) return email;
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return email;

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        return local.substring(0, 2) + "*".repeat(local.length() - 2) + domain;
    }

    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "-";  // null이나 빈 문자열인 경우 "-" 반환
        }

        // 전화번호 형식 정규화 (하이픈 제거)
        String normalizedNumber = phoneNumber.replaceAll("-", "");

        // 전화번호 형식 검증
        if (!normalizedNumber.matches("\\d{10,11}")) {
            return phoneNumber;  // 유효하지 않은 형식이면 원본 반환
        }

        // 마스킹 처리
        return normalizedNumber.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
    }

    public String maskAddress(String address) {
        if (address == null) return address;
        String[] parts = address.split(" ");
        if (parts.length <= 2) return address;

        return parts[0] + " " + parts[1] + " ***";
    }

    // DTO에 마스킹 처리된 데이터를 담아서 반환
    public Map<String, Object> getParentWithMaskedInfo(Parent parent) {
        Member member = memberRepository.findByEmail(parent.getMemberEmail());
        Map<String, Object> maskedInfo = new HashMap<>();

        maskedInfo.put("parentId", parent.getParentId());
        maskedInfo.put("name", maskName(member.getName()));
        maskedInfo.put("email", maskEmail(parent.getMemberEmail()));
        maskedInfo.put("phone", maskPhoneNumber(member.getPhone()));
        maskedInfo.put("status", parent.getRegistrationStatus());
        maskedInfo.put("createdDate", parent.getCreatedDate());
        maskedInfo.put("approvedAt", parent.getApprovedAt());

        return maskedInfo;
    }

    // 목록 조회 시 마스킹 처리
    public Page<Map<String, Object>> getPendingRegistrationsWithMasking(Pageable pageable) {
        Page<Parent> pendingRequests = parentRepository.findByRegistrationStatus(
                RegistrationStatus.PENDING, pageable);

        return pendingRequests.map(this::getParentWithMaskedInfo);
    }

    // 상세 정보 조회
    public ParentConsentDetailDTO getParentDetailWithMasking(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 학부모를 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(parent.getMemberEmail());

        ParentConsentDetailDTO dto = ParentConsentDetailDTO.from(parent, member);

        // 마스킹 처리
        dto.setName(maskName(dto.getName()));
        dto.setEmail(maskEmail(dto.getEmail()));
        dto.setPhone(maskPhoneNumber(dto.getPhone()));
        dto.setChildrenEmergencyPhone(maskPhoneNumber(dto.getChildrenEmergencyPhone()));
        dto.setAddress(maskAddress(dto.getAddress()));
        dto.setDetailAddress("***");

        return dto;
    }
}
