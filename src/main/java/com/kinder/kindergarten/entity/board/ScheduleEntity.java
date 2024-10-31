package com.kinder.kindergarten.entity.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="schedule")
@Getter @Setter
@ToString
public class ScheduleEntity {

  @Id
  @Column(name="schedule_id")
  private String scheduleId;

  @Column(nullable = false)
  private String scheduleTitle;

  @Column(nullable = false)
  private String scheduleContent;

  @Column(nullable = false)
  private String location;

  @Column
  private String start;

  @Column
  private String end;         // schedule_time (종료)
}
