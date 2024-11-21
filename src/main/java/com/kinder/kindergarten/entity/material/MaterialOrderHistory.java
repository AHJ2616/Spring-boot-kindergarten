package com.kinder.kindergarten.entity.material;

import com.kinder.kindergarten.constant.material.MaterialOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "material_order_history")
@Getter
@Setter
public class MaterialOrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private MaterialEntity material;

    @Enumerated(EnumType.STRING)
    private MaterialOrderStatus materialOrderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime statusChangeDate;
    private String orderDetails;

    @PrePersist
    public void prePersist() {
        this.orderDate = LocalDateTime.now();
        this.statusChangeDate = LocalDateTime.now();
    }
}