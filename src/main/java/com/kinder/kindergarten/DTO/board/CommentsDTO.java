package com.kinder.kindergarten.DTO.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentsDTO {

  private String commentsId;
  private String writer;
  private String contents;
  private LocalDateTime regiDate;
  private String boardId;
}
