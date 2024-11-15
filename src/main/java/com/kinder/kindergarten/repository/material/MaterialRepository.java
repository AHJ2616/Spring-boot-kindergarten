package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.entity.material.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MaterialRepository extends JpaRepository<MaterialEntity, String>,
        QuerydslPredicateExecutor<MaterialEntity>, MaterialRepositoryCustom {

    List<MaterialEntity> findByMaterialIdOrderByMaterialIdAsc(String materialId);
}


