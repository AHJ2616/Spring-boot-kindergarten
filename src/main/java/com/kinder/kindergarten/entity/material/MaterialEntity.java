package com.kinder.kindergarten.entity.material;

import com.kinder.kindergarten.DTO.material.MaterialFormDTO;
import com.kinder.kindergarten.constant.material.MaterialOrderStatus;
import com.kinder.kindergarten.constant.material.MaterialStatus;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name ="materialRe")
@Getter
@Setter
@ToString
public class MaterialEntity extends TimeEntity {

    @Id
    @Column(name = "materialId")
    private String materialId;// 자재 코드

    @Column(nullable = false)
    private String materialName; // 자재 이름

    @Lob
    @Column(nullable = false)
    private String materialDetail; // 자재 설명

    @Column(nullable = false)
    private String materialCategory; // 자재 분류

    @Column(nullable = false)
    private Integer materialPrice; // 자재 가격

    @Column(nullable = false)
    private Integer materialEa; // 자재 재고

    @Column(nullable = false)
    private String materialRemark; // 비고란

    @Column(nullable = false)
    private LocalDate materialRegdate; // 자재 입고일

    @Column(nullable = false)
    private String materialWriter; // 글작성자


    @Column(nullable = false)
    private String materialWriterEmail; // 글작성자 이메일


    @Enumerated(EnumType.STRING)   //enum 타입 매핑
    private MaterialStatus materialStatus; //자재 상태

    // 장바구니 후처리 구현 2024 11 13
    @Enumerated(EnumType.STRING)
    private MaterialOrderStatus orderStatus; // 주문 상태 (ORDERED, CANCELED, COMPLETED)





    public void updateMaterial(MaterialFormDTO materialFormDTO){

        this.materialWriterEmail = materialFormDTO.getMaterialWriterEmail();
        this.materialWriter = materialFormDTO.getMaterialWriter();
        this.materialName = materialFormDTO.getMaterialName();
        this.materialDetail = materialFormDTO.getMaterialDetail();
        this.materialCategory = materialFormDTO.getMaterialCategory();
        this.materialPrice = materialFormDTO.getMaterialPrice();
        this.materialEa = materialFormDTO.getMaterialEa();
        this.materialRemark = materialFormDTO.getMaterialRemark();
        this.materialRegdate = materialFormDTO.getMaterialRegdate();
        this.materialStatus = materialFormDTO.getMaterialStatus();
    }

}
