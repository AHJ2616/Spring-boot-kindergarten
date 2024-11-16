package com.kinder.kindergarten.DTO.parent;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentConsentDTO {

    // 학부모의 동의서에 관한 DTO

    private Long parentId; //학부모 고유 ID

    private Long parentConsentId;   // 동의서 ID

    private Boolean termsConsent;   //  서비스 이용 약관 동의

    private Boolean photoConsent;   // 사진 및 영상 촬영/활용 동의서

    private Boolean medicalInfoConsent; //  의료 정보 활용 동의서

    private Boolean communityConsent;   //  커뮤니티 활동 동의서

    private Boolean privateConsent; // 개인정보 수집, 이용 및 제 3자 제공 동의서

    private Boolean emergencyInfoConsent;   // 비상 연락망 및 응급 상황 동의서
}
