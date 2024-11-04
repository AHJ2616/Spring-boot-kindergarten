package com.kinder.kindergarten.entity.children;

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

  @OneToMany(mappedBy = "assignedClass")
  private List<Children> children;  // 반에 배정된 원아들

}


