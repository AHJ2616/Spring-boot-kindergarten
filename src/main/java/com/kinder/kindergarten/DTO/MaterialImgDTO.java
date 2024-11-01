package com.kinder.kindergarten.DTO;

import com.kinder.kindergarten.entity.MaterialImgEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class MaterialImgDTO {

  private Long id;

  private String material_imgName; // 이미지 파일명

  private String material_orgImgName; // 원본 이미지 파일명

  private String material_ImgUrl; // 이미지 조회 경로

  private String material_repimgYn; // 대표 이미지 여부

  private static ModelMapper modelMapper = new ModelMapper();

  public static MaterialImgDTO of(MaterialImgEntity materialImgEntity){
    return modelMapper.map(materialImgEntity, MaterialImgDTO.class);
  }


}