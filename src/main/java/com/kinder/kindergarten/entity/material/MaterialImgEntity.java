package com.kinder.kindergarten.entity.material;

import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="material_imgKl")
@Getter
@Setter
public class MaterialImgEntity extends TimeEntity {

    @Id
    @Column(name="material_img_id")
    private String id;

    private String material_imgName; // 이미지 파일명

    private String material_orgImgName; // 원본 이미지 파일명

    private String materialImgUrl; // 이미지 조회 경로

    private String material_repimgYn; // 대표 이미지 여부


    @OneToOne
    @JoinColumn(name = "material_idk") // 참조를 명확히 지정
    private MaterialEntity materialEntity;

    public void updateMaterialImg(String material_orgImgName, String material_imgName, String materialImgUrl) {
        this.material_orgImgName = material_orgImgName;
        this.material_imgName = material_imgName;
        this.materialImgUrl = materialImgUrl;
    }




}
