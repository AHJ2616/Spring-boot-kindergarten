package com.kinder.kindergarten.DTO.parent;

import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.parent.Parent;
import com.kinder.kindergarten.entity.parent.ParentConsent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParentConsentDetailDTO {

    // ERP에서 학부모 등록 승인 관리에서 상세보기 DTO(학부모 동의, 약관 페이지에서 학부모가 직접 작성한 정보)


    private Long parentId;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String childrenEmergencyPhone;
    private String parentAddress;
    private String detailAddress;
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

    // Parent 엔티티로부터 DTO 생성
    public static ParentConsentDetailDTO from(Parent parent) {
        ParentConsentDetailDTO dto = new ParentConsentDetailDTO();
        dto.setParentId(parent.getParentId());
        dto.setParentName(parent.getParentName());
        dto.setParentEmail(parent.getParentEmail());
        dto.setParentPhone(parent.getParentPhone());
        dto.setChildrenEmergencyPhone(parent.getChildrenEmergencyPhone());
        dto.setParentAddress(parent.getParentAddress());
        dto.setDetailAddress(parent.getDetailAddress());
        dto.setRegistrationStatus(parent.getRegistrationStatus());
        dto.setCreatedDate(parent.getCreatedDate());
        dto.setApprovedAt(parent.getApprovedAt());
        dto.setApprovedBy(parent.getApprovedBy());
        dto.setRejectReason(parent.getRejectReason());

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
