package com.kinder.kindergarten.DTO.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

  private String backgroundColor = "#3788d8";

  private String textColor = "#ffffff";

  private boolean allDay = false;
  private String start;       // schedule_time (시작)
  private String end;         // schedule_time (종료)

  public boolean isValid() {
    return id != null && title != null && start != null && end != null;
  }

}
