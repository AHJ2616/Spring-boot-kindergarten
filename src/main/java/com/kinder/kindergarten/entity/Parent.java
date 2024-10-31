package com.kinder.kindergarten.entity;

import com.kinder.kindergarten.constant.Children_Role;
import com.kinder.kindergarten.DTO.ParentFormDTO;
import com.kinder.kindergarten.DTO.ParentUpdateFormDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Entity // 테이블 담당한다.
@Getter
@Setter
@ToString
@Table(name = "parent")
public class Parent {

    /*
    학부모의 정보를 관리하는데 테이블, 회원가입 할 때 받는 정보
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parent_id; //학부모 고유 ID

    @Column(nullable = false)
    private String parent_name; // 학부모 이름

    @Column(unique = true, nullable = false)
    private String parent_email;    // 학부모 이메일(학부모 구분을 위해 유니크 설정)

    @Column(nullable = false)
    private String parent_pw;   // 학부모 비밀번호

    @Column(nullable = false)
    private String parent_phone;    // 학부모 핸드폰 번호

    @Column(nullable = false)
    private String parent_address;  // 학부모 주소

    @Column(nullable = false)
    private String parent_birthDate;    // 학부모 생년월일(yyyy-MM-dd)  10.30 추가(아이디 찾기 기능 구현)

    @Enumerated(EnumType.STRING)
    private Children_Role children_role; // 권한

    @Column(nullable = false)
    private LocalDate parent_createDate = LocalDate.now();    // 생성 날짜

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Children> children;
    /*
    하나의 학부모는 여러 자식이 있을 수 있기 때문에 @OneToMany 관계로 설정

    여기서, cascade = CascadeType.ALL -> 부모를 통해 자녀의 데이터를 함께 관리할 수 있도록 설정, ex) 부모 데이터 저장, 삭제 시 자식 데이터도 저장, 삭제
    orphanRemoval = true : 자녀가 부모와의 관계에서 제거 될 경우 해당 된 자녀 데이터도 자동으로 삭제 한다.

     */

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

     */
    public static Parent createParent(ParentFormDTO parentFormDTO, PasswordEncoder passwordEncoder) {
        // Parent 엔티티 생성하는 메서드

        Parent parent = new Parent();

        parent.setParent_name(parentFormDTO.getParent_name());
        parent.setParent_email(parentFormDTO.getParent_email());
        parent.setParent_birthDate(parentFormDTO.getParent_birthDate());
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



}
