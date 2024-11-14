package com.kinder.kindergarten.entity.children;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity // 테이블 담당한다.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "GrowthRecord")
public class GrowthRecord {

  // 원아의 성장 과정이나 일일 활동을 관리하는 엔티티

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long growthId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "childrenId", nullable = false)
  private Children children;

  @Column(nullable = false)
  private LocalDate growthDate;  // 활동 날짜

  @Column(nullable = false)
  private String growthActivity;  // 활동 내용

  @Column(length = 255)
  private String growthNotes;     // 성장 과정에 대한 추가 메모

  /*
  Hibernate:
    create table growth_record (
        growth_id bigint not null auto_increment,
        growth_activity varchar(255) not null,
        growth_date date not null,
        growth_notes varchar(255),
        children_id bigint not null,
        primary key (growth_id)
    ) engine=InnoDB

Hibernate:
    alter table if exists growth_record
       add constraint FKnh9gstykweriv1yp73f1hgdk8
       foreign key (children_id)
       references children (children_id)
   */
}