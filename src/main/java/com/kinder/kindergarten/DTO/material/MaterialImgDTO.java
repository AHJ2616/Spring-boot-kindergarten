package com.kinder.kindergarten.DTO.material;

import com.kinder.kindergarten.entity.material.MaterialImgEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class MaterialImgDTO {

    private String id;

    private String material_imgName; // 이미지 파일명

    private String material_orgImgName; // 원본 이미지 파일명

    private String materialImgUrl; // 이미지 조회 경로

    private String material_repimgYn; // 대표 이미지 여부

    private static ModelMapper modelMapper = new ModelMapper();

    public static MaterialImgDTO of(MaterialImgEntity materialImgEntity){
        return modelMapper.map(materialImgEntity, MaterialImgDTO.class);
    }


}
