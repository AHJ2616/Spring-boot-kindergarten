package com.kinder.kindergarten.entity.material;

import com.kinder.kindergarten.DTO.material.MaterialFormDTO;
import com.kinder.kindergarten.constant.material.MaterialStatus;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name ="materialkl")
@Getter
@Setter
@ToString
public class MaterialEntity extends TimeEntity {

    @Id
    @Column(name = "material_idK")
    private String id;// 자재 코드

    @Column(nullable = false)
    private String material_name; // 자재 이름

    @Lob
    @Column(nullable = false)
    private String material_detail; // 자재 설명

    @Column(nullable = false)
    private String material_category; // 자재 분류

    @Column(nullable = false)
    private Integer material_price; // 자재 가격

    @Column(nullable = false)
    private Integer material_ea; // 자재 재고

    @Column(nullable = false)
    private String material_remark; // 비고란

    @Column(nullable = false)
    private LocalDate material_regdate; // 자재 입고일

    @Enumerated(EnumType.STRING)   //enum 타입 매핑
    private MaterialStatus materialStatus; //자재 상태

    /*@OneToMany(mappedBy = "materialEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MaterialImgEntity> materialImgEntities;*/

    public void updateMaterial(MaterialFormDTO materialFormDTO){

        this.material_name = materialFormDTO.getMaterial_name();
        this.material_detail = materialFormDTO.getMaterial_detail();
        this.material_category = materialFormDTO.getMaterial_category();
        this.material_price = materialFormDTO.getMaterial_price();
        this.material_ea = materialFormDTO.getMaterial_ea();
        this.material_remark = materialFormDTO.getMaterial_remark();
        this.material_regdate = materialFormDTO.getMaterial_regdate();
        this.material_name = materialFormDTO.getMaterial_name();
        this.materialStatus = materialFormDTO.getMaterialStatus();
    }

}
