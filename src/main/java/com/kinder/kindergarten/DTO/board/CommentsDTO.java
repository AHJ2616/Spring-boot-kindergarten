package com.kinder.kindergarten.DTO.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentsDTO {

  private String commentsId;
  private String writer;
  private String contents;
  private LocalDateTime regiDate;
  private String boardId;
}
