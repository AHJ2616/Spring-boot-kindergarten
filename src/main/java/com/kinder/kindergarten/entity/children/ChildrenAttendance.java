package com.kinder.kindergarten.entity.children;

import com.kinder.kindergarten.entity.parent.Parent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "ChildrenAttendance")
public class ChildrenAttendance {

  // 원아의 일일 출석 기록을 관리하는 엔티티

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long attendanceId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "childrenId", nullable = false)
  private Children children;

  @Column(nullable = false)
  private LocalDate attendanceDate; // 출석 일자

  @Column(nullable = false)
  private LocalDateTime checkInTime; // 등원 시간

  @Column(nullable = false)
  private LocalDateTime checkOutTime; // 하원 시간

  @Column(length = 100)
  private String attendanceStatus;   // 출석 상태 (출석, 지각, 결석 등)

}