package com.kinder.kindergarten.entity.children;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "HealthRecord")
public class HealthRecord {

  // 원아의 건강 정보를 기록하는 테이블

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long healthRecordId; // 건강 기록 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "childrenId", nullable = false)
  private Children children;

  @Column(nullable = false)
  private LocalDate healthDate;  // 검진 날짜

  private Double healthTemperature;  // 체온

  private Double healthHeight;    // 키(cm)

  private Double healthWeight;    // 몸무게(kg)

  @Column(length = 255, nullable = false)
  private String healthStatus; // 건강 상태 (예: 양호, 주의 필요 등)

  private String healthAllergies; // 알레르기 정보

  private String healthMedicalHistory; // 병력 정보

  private String healthMealStatus;  // 식사 상태

  private String healthSleepStatus;  // 수면 상태

  @Column(length = 255)
  private String healthNotes; // 기타 성장 관련 메모

  /*
  Hibernate:
    create table health_record (
        health_record_id bigint not null auto_increment,
        health_allergies varchar(255),
        health_date date not null,
        health_height float(53),
        health_meal_status varchar(255),
        health_medical_history varchar(255),
        health_notes varchar(255),
        health_sleep_status varchar(255),
        health_status varchar(255) not null,
        health_temperature float(53),
        health_weight float(53),
        children_id bigint not null,
        primary key (health_record_id)
    ) engine=InnoDB

Hibernate:
    alter table if exists health_record
       add constraint FKpalm39r16l7almohxufmw3x4g
       foreign key (children_id)
       references children (children_id)
   */
}