package com.kinder.kindergarten.DTO.parent;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentConsentDTO {

    // 학부모의 동의서에 관한 DTO

    private Long parentId; // 학부모 고유 ID

    private Long parentConsentId;   // 동의서 ID

    @Builder.Default
    private Boolean termsConsent = false;   // 서비스 이용 약관 동의

    @Builder.Default
    private Boolean photoConsent = false;   // 사진 및 영상 촬영/활용 동의서

    @Builder.Default
    private Boolean medicalInfoConsent = false; // 의료 정보 활용 동의서

    @Builder.Default
    private Boolean communityConsent = false;   // 커뮤니티 활동 동의서

    @Builder.Default
    private Boolean privateConsent = false; // 개인정보 수집, 이용 및 제 3자 제공 동의서

    @Builder.Default
    private Boolean emergencyInfoConsent = false;   // 비상 연락망 및 응급 상황 동의서

    private int consentStep; // 동의 단계 (1 또는 2)

    // 모든 동의 항목 체크 여부 확인
    public boolean isAllConsented() {
        return termsConsent && photoConsent && medicalInfoConsent &&
                communityConsent && privateConsent && emergencyInfoConsent;
    }

    // 1단계 동의 항목 체크 여부 확인
    public boolean isFirstStepConsented() {
        return termsConsent && communityConsent && privateConsent;
    }

    // 2단계 동의 항목 체크 여부 확인
    public boolean isSecondStepConsented() {
        return photoConsent && medicalInfoConsent && emergencyInfoConsent;
    }
}
