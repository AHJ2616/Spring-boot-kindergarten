package com.kinder.kindergarten.entity.material;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_order_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialOrderHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String historyId;

    private String orderMaterialName;

    private String orderMaterialDetail;

    private Integer orderMaterialPrice;

    private Integer quantity;

    private Integer orderMaterialTotalPrice; // 주문 수량 x 주문 갯수

    private String status;

    private String rejectReason; // 반려 사유

    private String orderWriter; // 작성자

    private String orderWriterEmail; // 작성자

    private LocalDateTime orderDate;

    private LocalDate statusChangeDate;

    private LocalDateTime deletionDate;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialEntity material;
}