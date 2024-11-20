package com.kinder.kindergarten.DTO.material;

import com.kinder.kindergarten.entity.material.MaterialEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MaterialOrderHistoryDTO {

    private String historyId;

    private String orderMaterialName;

    private String orderMaterialDetail;

    private Integer orderMaterialPrice;

    private Integer quantity;

    private Integer orderMaterialTotalPrice; // 주문 수량 x 주문 갯수

    private String status;

    private LocalDateTime orderDate;

    private LocalDate statusChangeDate;

    private LocalDateTime deletionDate;

    private MaterialEntity material;

    private String rejectReason; // 반려 사유

    private String orderWriter; // 작성자

    private String orderWriterEmail; // 작성자

}
