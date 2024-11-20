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

    private String orderMaterialDetail; // 주문 한 자재 상세 설명

    private Integer orderMaterialPrice; // 주문한 자재 한개당 단가

    private Integer orderMaterialTotalPrice; // 주문 수량 x 주문 갯수

    private Integer quantity;

    private String status; // PENDING, COMPLETED

    private String orderWriter; // 작성자

    private String orderWriterEmail; // 작성자 이메일

    private LocalDateTime orderDate;

}
