package com.kinder.kindergarten.DTO.board;

import com.kinder.kindergarten.constant.board.BoardType;
import com.kinder.kindergarten.entity.board.BoardEntity;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardFormDTO {

  //기본키
  private String boardId;

  //제목
  private String boardTitle;

  //게시글 내용
  private String boardContents;

  //게시글 유형
  private BoardType boardType;

  //작성자
  private String boardWriter;

  //첨부파일 키
  private String fileId;

  //상품 저장후, 수정시 이미지 정보를 저장하는 리스트
  private List<BoardFileDTO> BoardFileList = new ArrayList<>();

  private List<Long> FileIds = new ArrayList<>();

  private static ModelMapper modelMapper = new ModelMapper();

  public BoardEntity wirteBoard(){
    return modelMapper.map(this,BoardEntity.class);
  }
  public static BoardFormDTO of(BoardEntity board){
    return modelMapper.map(board, BoardFormDTO.class);
  }
//ModelMapper는 entoty와 DTO간의 데이터를 복사시켜주는 메소드이다.


}
