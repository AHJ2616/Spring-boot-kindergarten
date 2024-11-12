package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity,String> {
}
