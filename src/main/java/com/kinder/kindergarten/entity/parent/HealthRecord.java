package com.kinder.kindergarten.entity.parent;

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
  @JoinColumn(name = "parentId", nullable = false)
  private Parent children;

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
}