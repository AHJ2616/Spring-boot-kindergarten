package com.kinder.kindergarten.entity.children;

import com.kinder.kindergarten.entity.parent.Parent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "classChangeHistory")
public class ClassChangeHistory {

  // 원아가 반 변경할 때 그 내역을 기록하는 엔티티

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long historyId; // 변경 이력 고유 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "childrenId", nullable = false)
  private Children children;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "previousClassId")
  private ClassRoom previousClass; // 이전 반

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "newClassId", nullable = false)
  private ClassRoom newClass; // 새 반

  @Column(nullable = false)
  private LocalDate changeDate; // 반 변경 일자


}