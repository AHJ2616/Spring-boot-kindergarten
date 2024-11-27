package com.kinder.kindergarten.entity.parent;

import com.kinder.kindergarten.constant.parent.ParentType;
import com.kinder.kindergarten.constant.parent.RegistrationStatus;
import com.kinder.kindergarten.entity.children.Children;
import com.kinder.kindergarten.entity.children.ChildrenBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "parent")
@AllArgsConstructor
@NoArgsConstructor
public class Parent extends ChildrenBaseEntity {

    /*
    학부모 정보를 관리하는 테이블
     */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long parentId; //학부모 고유 ID (PK)

  @Column(name = "member_email")
  private String memberEmail;  // Member 엔티티 참조용 이메일


  /*
  @Column(nullable = false)
  private String parentName; // 학부모 성함

  @Column(unique = true, nullable = false)
  private String  parentEmail;    // 학부모 이메일(학부모 구분을 위해 유니크 설정)

  @Column
  private String parentPassword;   // 학부모 비밀번호

  @Column(nullable = false)
  private String parentAddress;  // 학부모 주소

  @Column(nullable = false)
  private String parentPhone;    // 학부모 핸드폰 번호

  @Enumerated(EnumType.STRING)
  private Children_Role childrenRole; // 권한

   */

  @Column(name = "childrenEmergencyPhone")
  private String childrenEmergencyPhone;//	긴급 연락처	(선택사항)

  private String detailAddress; // 상세 주소 -> 11.19 추가


  @Enumerated(EnumType.STRING)
  private ParentType parentType;    // 자녀와의 관계


/*  @Column(nullable = false)
  @Builder.Default
  private LocalDate parentCreateDate = LocalDate.now();    // 생성 날짜
 */

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Children> children;  // 여러 자녀를 가질 수 있음
   /*
    하나의 학부모는 여러 자식이 있을 수 있기 때문에 @OneToMany 관계로 설정

    여기서, cascade = CascadeType.ALL -> 부모를 통해 자녀의 데이터를 함께 관리할 수 있도록 설정, ex) 부모 데이터 저장, 삭제 시 자식 데이터도 저장, 삭제
    orphanRemoval = true : 자녀가 부모와의 관계에서 제거 될 경우 해당 된 자녀 데이터도 자동으로 삭제 한다.

     */

  // ERP 등록인지 회원가입인지 구분하는 필드
  @Column(nullable = false)
  @Builder.Default  // Builder 패턴 사용시 기본값 설정
  private Boolean isErpRegistered = false;  // false: 회원가입, true: ERP등록


//  // 회원가입 시에만 사용되는 필드들의 유효성을 검사하는 메서드
//  @PrePersist
//  @PreUpdate
//  public void validateFields() {
//    // ERP 등록이 아닌 경우에는 기본 필수 필드만 검증
//    if (!isErpRegistered) {
//      if (parentEmail == null || parentName == null || parentPhone == null || parentAddress == null) {
//        throw new IllegalStateException("필수 정보가 누락되었습니다.");
//      }
//    }
//  }

  @Enumerated(EnumType.STRING)
  @Column(name = "registration_status", nullable = false)
  private RegistrationStatus registrationStatus;  //학부모 상태

  @Column(name = "reject_reason")
  private String rejectReason;  // 반려 사유

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;// 승인 일시

  @Column(name = "approved_by")
  private String approvedBy;// 승인자

  @OneToOne(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private ParentConsent parentConsent;  // ParentConsent와의 1:1 관계 추가

  /*
  Hibernate:
    create table parent (
        parent_id bigint not null auto_increment,
        created_date datetime(6),
        updated_date datetime(6),
        children_emergency_phone varchar(255),
        is_erp_registered bit not null,
        parent_address varchar(255),
        parent_email varchar(255),
        parent_name varchar(255) not null,
        parent_password varchar(255),
        parent_phone varchar(255),
        parent_type enum ('AUNT','FATHER','GRANDFATHER','GRANDMOTHER','GUARDIAN','MOTHER','UNCLE'),
        primary key (parent_id)
    ) engine=InnoDB

    Hibernate:
    alter table if exists parent
       drop index if exists UK_qkomq02jt7yw7pan7rttcitn4
Hibernate:
    alter table if exists parent
       add constraint UK_qkomq02jt7yw7pan7rttcitn4 unique (parent_email)
   */
}