package com.kinder.kindergarten.entity.parent;

import com.kinder.kindergarten.constant.parent.Children_Role;
import com.kinder.kindergarten.entity.children.Children;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "parent")
@AllArgsConstructor
@NoArgsConstructor
public class Parent {

    /*
    학부모 정보를 관리하는 테이블
     */


  // 학부모 정보
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long parentId; //학부모 고유 ID (PK)

  @Column(nullable = false)
  private String parentName; // 학부모 이름

  @Column(unique = true, nullable = false)
  private String  parentEmail;    // 학부모 이메일(학부모 구분을 위해 유니크 설정)

  @Column(nullable = false)
  private String parentPassword;   // 학부모 비밀번호

  @Column(nullable = false)
  private String parentPhone;    // 학부모 핸드폰 번호

  private String childrenEmergencyPhone;//	긴급 연락처	(선택사항)

  @Column(nullable = false)
  private String parentAddress;  // 학부모 주소

  @Enumerated(EnumType.STRING)
  private Children_Role childrenRole; // 권한

  @Column(nullable = false)
  @Builder.Default
  private LocalDate parentCreateDate = LocalDate.now();    // 생성 날짜

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Children> children;  // 여러 자녀를 가질 수 있음
   /*
    하나의 학부모는 여러 자식이 있을 수 있기 때문에 @OneToMany 관계로 설정

    여기서, cascade = CascadeType.ALL -> 부모를 통해 자녀의 데이터를 함께 관리할 수 있도록 설정, ex) 부모 데이터 저장, 삭제 시 자식 데이터도 저장, 삭제
    orphanRemoval = true : 자녀가 부모와의 관계에서 제거 될 경우 해당 된 자녀 데이터도 자동으로 삭제 한다.

     */

}