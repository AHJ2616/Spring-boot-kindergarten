package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.entity.material.MaterialFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialFileRepository extends JpaRepository<MaterialFileEntity, String> {

    List<MaterialFileEntity> findByMaterialEntity_materialId(String materialId);
}