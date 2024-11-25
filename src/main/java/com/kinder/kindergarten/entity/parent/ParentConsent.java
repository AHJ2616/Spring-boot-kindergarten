package com.kinder.kindergarten.entity.parent;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentConsent {

    // ERP 및 커뮤니티 사이트 이용하기 앞서 학부모 에게 받아야 할 동의서에 관한 Entity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentConsentId;   // 동의서 ID

    private Boolean termsConsent;   //  서비스 이용 약관 동의

    private Boolean photoConsent;   // 사진 및 영상 촬영/활용 동의서

    private Boolean medicalInfoConsent; //  의료 정보 활용 동의서

    private Boolean communityConsent;   //  커뮤니티 활동 동의서

    private Boolean privateConsent; // 개인정보 수집, 이용 및 제 3자 제공 동의서

    private Boolean emergencyInfoConsent;   // 비상 연락망 및 응급 상황 동의서

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;  // 학부모 엔티티와의 1:1 관계 매핑

    private int consentStep; // 단계별 동의 단계 -> 11.25 추가

    @Column(name = "consent_complete_date")
    private LocalDateTime consentCompleteDate; // 동의 완료 시점 -> 11.25 추가

    /*

    Hibernate:
    create table parent_consent (
        parent_consent_id bigint not null auto_increment,
        community_consent bit,
        emergency_info_consent bit,
        medical_info_consent bit,
        photo_consent bit,
        private_consent bit,
        terms_consent bit,
        parent_id bigint,
        primary key (parent_consent_id)
    ) engine=InnoDB
Hibernate:
    alter table if exists parent_consent
       drop index if exists UK_rb2yjqt6ib4al5xvotma74xff
Hibernate:
    alter table if exists parent_consent
       add constraint UK_rb2yjqt6ib4al5xvotma74xff unique (parent_id)
Hibernate:
    alter table if exists parent_consent
       add constraint FKnnui3g8ew5167hxfigody0pra
       foreign key (parent_id)
       references parent (parent_id)

     */
}
