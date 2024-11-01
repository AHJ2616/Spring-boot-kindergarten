package com.kinder.kindergarten.entity.parent;

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
    @JoinColumn(name = "parentId", nullable = false)
    private Parent children;

    @Column(nullable = false)
    private LocalDate growth_date;  // 활동 날짜

    @Column(nullable = false)
    private String growth_activity;  // 활동 내용

    @Column(length = 255)
    private String growth_notes;     // 성장 과정에 대한 추가 메모
}
