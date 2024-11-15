package com.kinder.kindergarten.DTO.material;


import com.kinder.kindergarten.entity.material.MaterialEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MaterialOrderDTO {


    private String orderId;

    private String orderMaterialName;

    private Integer quantity;

    private String status; // PENDING, COMPLETED

    private LocalDateTime orderDate;

}
