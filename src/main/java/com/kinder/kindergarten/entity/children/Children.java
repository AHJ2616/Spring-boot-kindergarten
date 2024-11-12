package com.kinder.kindergarten.entity.children;


import com.kinder.kindergarten.entity.parent.Parent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity // 테이블 담당한다.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "children")
public class Children extends ChildrenBaseEntity {

    // 원아의 기본 정보를 관리하는 테이블
    // assignedClassId를 통해 반 배정 테이블과 연결

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childrenId ; // 원아의 고유 ID

    @Column(nullable = false)
    private String childrenName; //원아 이름

    @Column(nullable = false)
    private LocalDate childrenBirthDate ; // 원아 생년월일

    @Column(nullable = false)
    private String childrenGender;  // 원아의 성별

    @Column(nullable = false)
    private String childrenBloodType ;  // 원아의 혈액형

    //private String	parentName; //학부모 성함

    private String	childrenAllergies;  //알레르기 정보

    private String	childrenMedicalHistory; //병력 정보

    @Lob
    private String	childrenNotes; // 기타 사항

 /*   @Column(nullable = false)
    @Builder.Default
    private LocalDate childrenCreateDate = LocalDate.now();    // 생성 날짜
  */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classRoomId")
    private ClassRoom assignedClass;    // 원아 반

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Parent parent; // 부모 정보 참조

    /*
    Hibernate:
    create table children (
        children_id bigint not null auto_increment,
        created_date datetime(6),
        updated_date datetime(6),
        children_allergies varchar(255),
        children_birth_date date not null,
        children_blood_type varchar(255) not null,
        children_gender varchar(255) not null,
        children_medical_history varchar(255),
        children_name varchar(255) not null,
        children_notes tinytext,
        parent_name varchar(255) not null,
        class_room_id bigint,
        parent_id bigint,
        primary key (children_id)
    ) engine=InnoDB

    Hibernate:
    alter table if exists children
       add constraint FKd0c9woqafnv8awh2ws30n5kly
       foreign key (class_room_id)
       references class_room (class_room_id)

       Hibernate:
    alter table if exists children
       add constraint FKtgapw1m5c4v0ig4ekfg79fnes
       foreign key (parent_id)
       references parent (parent_id)
     */
}
