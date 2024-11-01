package com.kinder.kindergarten.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScheduleDTO {

  //기본키
  private String scheduleId;

  //일정 제목
  private String scheduleTitle;

  //일정 내용
  private String scheduleContent;

  //장소
  private String location;

  private String start;       // schedule_time (시작)
  private String end;         // schedule_time (종료)

  public ScheduleDTO(String scheduleTitle,String start){
    this.scheduleTitle = scheduleTitle;
    this.start = start;
  }
}
