package com.kinder.kindergarten.DTO.material;


import com.kinder.kindergarten.entity.material.MaterialFileEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class MaterialFileDTO {

  // 기본키
  private String fileId;

  //원본 이름
  private String orignalName;

  //수정한 이름
  private String modifiedName;

  //파일 저장 경로
  private String filePath;

  //메인 사진 이름
  private String mainFile;

  //참고할 BoardEntity
  private String boardId;

  private static ModelMapper modelMapper = new ModelMapper();  // 맴버 변수로 객체 추가

  public static MaterialFileDTO of(MaterialFileEntity materialFileEntity){
    return modelMapper.map(materialFileEntity, MaterialFileDTO.class);
  }



}