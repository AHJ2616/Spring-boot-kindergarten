package com.kinder.kindergarten.entity.parent;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity // 테이블 담당한다.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "classRoom")
public class ClassRoom {

  // 해당 반의 정보와 담당 교사를 관리하는 엔티티

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long classRoomId;  // 반 ID

  @Column(nullable = false)
  private String classRoomName;  // 반 이름

  @Column(nullable = false)
  private String employeeName;// 담당 교사 이름

  @Column(nullable = false)
  private int maxChildren;// 반 최대 수용 인원 -> 11.01 추가

  private String classRoomDescription; // 반 설명 및 비고


}

/*
Hibernate:
    create table class_room (
        class_room_id bigint not null auto_increment,
        class_room_description varchar(255),
        class_room_name varchar(255) not null,
        employee_name varchar(255) not null,
        primary key (class_room_id)
    ) engine=InnoDB

    11.01 추가 -*
    Hibernate:
    alter table if exists class_room
       add column max_children integer not null
 */
