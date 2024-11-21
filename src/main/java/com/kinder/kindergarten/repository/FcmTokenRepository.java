package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity, Long> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있습니다.
}
