package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.entity.material.MaterialOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialOrderRepository extends JpaRepository<MaterialOrderEntity, String> {
}
