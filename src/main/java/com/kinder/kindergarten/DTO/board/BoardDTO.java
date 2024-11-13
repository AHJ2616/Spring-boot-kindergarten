package com.kinder.kindergarten.DTO.board;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.constant.board.BoardType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardDTO {

  //기본키
  private String boardId;

  //제목
  private String boardTitle;

  //게시글 내용
  private String boardContents;

  //게시글 유형
  private BoardType boardType;

  //작성자
  private String email; //MemberDTO.email

  //작성자 이름
  private String writer;

  private int views;

  private LocalDateTime regiDate;

  private LocalDateTime modiDate;

  private List<BoardFileDTO> BoardFileList = new ArrayList<>();

  private List<Long> FileIds = new ArrayList<>();

  private boolean hasZipFile;

  private int fileCount;

  public boolean getHasZipFile() {
    if (BoardFileList == null) return false;
    return BoardFileList.stream().anyMatch(file -> "Y".equals(file.getIsZip()));
  }


}
