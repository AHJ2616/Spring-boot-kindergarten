package com.kinder.kindergarten.entity.parent;

import com.kinder.kindergarten.constant.Children_Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Table(name = "parent")
public class Parent {

    /*
    학부모와 원아의 정보를 관리하는데 테이블
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

    @Column(nullable = false)
    private String parentAddress;  // 학부모 주소

    @Column(nullable = false)
    private LocalDate parentBirthDate;    // 학부모 생년월일(yyyy-MM-dd)

    @Enumerated(EnumType.STRING)
    private Children_Role parentRole; // 권한

    @Column(nullable = false)
    private LocalDate parentCreateDate = LocalDate.now();    // 생성 날짜


    // 원아 정보

    @Column(nullable = false)
    private String childrenName;    // 원아 이름

    @Column(nullable = false)
    private LocalDate childrenBirthDate;    // 원아 생년월일

    private String childrenEmergencyPhone;  // 긴급 연락처(학부모)

    private String childrenAllergies;   // 알레르기 정보

    private String childrenMedicalHistory;  // 병력 정보

    private String childrenNotes;   // 기타 사항

    // 반 ID와 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignedClassId")
    private ClassRoom classRoom;    // 원아 반


    /*
      Hibernate:
    create table parent (
        parent_id bigint not null auto_increment,
        children_role enum ('PARENT','TEACHER'),
        parent_address varchar(255) not null,
        parent_create_date date not null,
        parent_email varchar(255) not null,
        parent_name varchar(255) not null,
        parent_phone varchar(255) not null,
        parent_pw varchar(255) not null,
        primary key (parent_id)
    ) engine=InnoDB
Hibernate:
    alter table if exists parent
       drop index if exists UK_qkomq02jt7yw7pan7rttcitn4
Hibernate:
    alter table if exists parent
       add constraint UK_qkomq02jt7yw7pan7rttcitn4 unique (parent_email)


    10.30 추가 - *
    Hibernate:
        alter table if exists parent
           add column parent_birth_date varchar(255) not null

    11.01 추가 - **
    Hibernate:
    alter table if exists parent
       modify column parent_birth_date date not null
     */


    /*
    public static Parent createParent(ParentFormDTO parentFormDTO, PasswordEncoder passwordEncoder) {
        // Parent 엔티티 생성하는 메서드

        Parent parent = new Parent();

        parent.setParentName(parentFormDTO.getParent_name());
        parent.setParentEmail(parentFormDTO.getParent_email());
        parent.setParentBirthDate(parentFormDTO.getParent_birthDate());
        String password = passwordEncoder.encode(parentFormDTO.getParent_pw());
        parent.setParent_pw(password);
        parent.setParent_phone(parentFormDTO.getParent_phone());
        parent.setParent_address(parentFormDTO.getParent_address());
        parent.setChildren_role(Children_Role.PARENT);

        return parent;

    }

    public static Parent updateParent(ParentUpdateFormDTO parentUpdateFormDTO) {
        // Parent 엔티티 수정 메서드

        Parent parent = new Parent();

        parent.setParent_name(parentUpdateFormDTO.getParent_name());
        parent.setParent_email(parentUpdateFormDTO.getParent_email());
        parent.setParent_phone(parentUpdateFormDTO.getParent_phone());
        parent.setParent_address(parentUpdateFormDTO.getParent_address());
        parent.setParent_birthDate(parentUpdateFormDTO.getParent_birthDate());

        return parent;
    }

     */



}
