package com.kinder.kindergarten.DTO.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
public class ScheduleDTO {


  //기본키
  private String id;

  //일정 제목
  private String title;

  //일정 내용
  private String description;

  private String type;

  private String username;

  private String backgroundColor;

  private String textColor;

  private boolean allDay;
  private LocalDateTime start;       // schedule_time (시작)
  private LocalDateTime end;         // schedule_time (종료)

}
