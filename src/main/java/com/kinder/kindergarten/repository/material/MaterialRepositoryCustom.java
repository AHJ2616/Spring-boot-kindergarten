package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.DTO.material.MaterialOrderDTO;
import com.kinder.kindergarten.DTO.material.MaterialSearchDTO;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialRepositoryCustom {

    Page<MaterialEntity> getMaterialPage(MaterialSearchDTO materialSearchDTO, Pageable pageable);

}
