package com.kinder.kindergarten.DTO.board;

import lombok.Data;

@Data
public class BoardFileDTO {

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

  // ZIP 파일 여부
  private String isZip;

}
