package com.kinder.kindergarten.entity.material;


import com.kinder.kindergarten.constant.material.MaterialOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "material_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    private String orderMaterialName; // 주문 한 자재 이름

    private String orderMaterialDetail; // 주문 한 자재 상세 설명

    private Integer orderMaterialPrice; // 주문한 자재 한개당 단가

    private Integer quantity; // 주문 수량

    private Integer orderMaterialTotalPrice; // 주문 수량 x 주문 갯수

/*    @Enumerated(EnumType.STRING)   //enum 타입 매핑
    private MaterialOrderStatus materialOrderStatus;*/

    private String status; // PENDING, COMPLETED
    
    private String orderWriter; // 작성자

    private String orderWriterEmail; // 작성자 이메일


    private LocalDateTime orderDate; // 주문 넣은 시간

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @PrePersist
    public void prePersist() {
        this.orderDate = LocalDateTime.now();
    }
}