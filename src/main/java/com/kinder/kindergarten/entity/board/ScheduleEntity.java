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
  private String id;

  private String title;
  private String description;
  private LocalDateTime start;
  private LocalDateTime end;
  private String type;
  private String backgroundColor;

  private String textColor;

  private boolean allDay;
  private String username;
}
