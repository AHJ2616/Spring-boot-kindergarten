package com.kinder.kindergarten.DTO.parent;

import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.entity.parent.ParentConsent;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParentConsentDetailDTO {

    // ERP에서 학부모 등록 승인 관리에서 상세보기 DTO(학부모 동의, 약관 페이지에서 학부모가 직접 작성한 정보)


    private Long parentId;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String childrenEmergencyPhone; // 비상연락처
    private String detailAddress;// 상세주소

    // 등록 상태 정보
    private RegistrationStatus registrationStatus;
    private LocalDateTime createdDate;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String rejectReason;

    // 동의 정보
    private Boolean termsConsent;
    private Boolean photoConsent;
    private Boolean medicalInfoConsent;
    private Boolean privateConsent;
    private Boolean communityConsent;
    private Boolean emergencyInfoConsent;

    // Parent 엔티티와 Member 엔티티로부터 DTO 생성
    public static ParentConsentDetailDTO from(Parent parent, Member member) {
        ParentConsentDetailDTO dto = new ParentConsentDetailDTO();

        // Parent 정보 설정
        dto.setParentId(parent.getParentId());
        dto.setChildrenEmergencyPhone(parent.getChildrenEmergencyPhone());
        dto.setDetailAddress(parent.getDetailAddress());
        dto.setRegistrationStatus(parent.getRegistrationStatus());
        dto.setCreatedDate(parent.getCreatedDate());
        dto.setApprovedAt(parent.getApprovedAt());
        dto.setApprovedBy(parent.getApprovedBy());
        dto.setRejectReason(parent.getRejectReason());

        // Member 정보 설정
        if (member != null) {
            dto.setName(member.getName());
            dto.setEmail(member.getEmail());
            dto.setPhone(member.getPhone());
            dto.setAddress(member.getAddress());
        }

        // 동의 정보 설정
        if (parent.getParentConsent() != null) {
            ParentConsent consent = parent.getParentConsent();
            dto.setTermsConsent(consent.getTermsConsent());
            dto.setPhotoConsent(consent.getPhotoConsent());
            dto.setMedicalInfoConsent(consent.getMedicalInfoConsent());
            dto.setPrivateConsent(consent.getPrivateConsent());
            dto.setCommunityConsent(consent.getCommunityConsent());
            dto.setEmergencyInfoConsent(consent.getEmergencyInfoConsent());
        }

        return dto;
    }
}
