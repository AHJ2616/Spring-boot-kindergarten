package com.kinder.kindergarten.repository.parent;

import com.kinder.kindergarten.entity.parent.ParentConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentConsentRepository extends JpaRepository<ParentConsent, Long> {

    Optional<ParentConsent> findByParent_ParentId(Long parentId);
    // 학부모 ID로 동의서 찾기

    boolean existsByParentParentId(Long parentId);
    // 동의서가 존재하는지 확인

    // 동의 상태 조회
    boolean existsByParent_ParentEmailAndTermsConsentIsTrue(String email);
    boolean existsByParent_ParentEmailAndCommunityConsentIsTrue(String email);
    boolean existsByParent_ParentEmailAndPrivateConsentIsTrue(String email);
    boolean existsByParent_ParentEmailAndPhotoConsentIsTrue(String email);
    boolean existsByParent_ParentEmailAndMedicalInfoConsentIsTrue(String email);
    boolean existsByParent_ParentEmailAndEmergencyInfoConsentIsTrue(String email);

    // 이메일 존재 여부 확인
    boolean existsByParent_ParentEmail(String email);
}
