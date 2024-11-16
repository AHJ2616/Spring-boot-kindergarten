package com.kinder.kindergarten.repository.parent;

import com.kinder.kindergarten.entity.parent.ParentConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentConsentRepository extends JpaRepository<ParentConsent, Long> {

    Optional<ParentConsent> findByParent_ParentId(Long parentId);
    // 학부모 ID로 동의서 찾기

    boolean existsByParentParentId(Long parentId);
    // 동의서가 존재하는지 확인
}
