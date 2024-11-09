package com.kinder.kindergarten.entity.children;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity // 테이블 담당한다.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "classRoom")
public class ClassRoom extends ChildrenBaseEntity {

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

  @Builder.Default
  @OneToMany(mappedBy = "assignedClass", cascade = CascadeType.ALL)
  private List<Children> children = new ArrayList<>();  // 반에 배정된 원아들

  @Builder.Default
  private Integer currentStudents = 0;  // 현재 원아 수

  public Integer getCurrentStudents() {
    return currentStudents != null ? currentStudents : 0;
  }

  public void setCurrentStudents(Integer currentStudents) {
    this.currentStudents = currentStudents != null ? currentStudents : 0;
  }

 /* private LocalDate ClassCreatedDate = LocalDate.now(); // 반 등록일

  private LocalDate ClassUpdatedDate; // 마지막 수정일

  */

  /*
  Hibernate:
    create table class_room (
        class_room_id bigint not null auto_increment,
        created_date datetime(6),
        updated_date datetime(6),
        class_room_description varchar(255),
        class_room_name varchar(255) not null,
        employee_name varchar(255) not null,
        max_children integer not null,
        primary key (class_room_id)
    ) engine=InnoDB
   */
}


