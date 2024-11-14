package com.kinder.kindergarten.entity.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Entity
@Table(name="schedule")
@Getter @Setter
@ToString
public class ScheduleEntity {

  @Id
  private String id;

  private String title;
  private String description;
  private String start;
  private String end;
  private String type;
  private String backgroundColor;

  private String textColor;

  private boolean allDay;
  private String username;
}
