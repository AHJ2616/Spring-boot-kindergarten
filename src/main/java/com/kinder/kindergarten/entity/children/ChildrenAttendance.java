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
  private Long attendanceId;  // 출석 기록 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "childrenId", nullable = false)
  private Children children;  // 원아 객체

  @Column(nullable = false)
  private LocalDate attendanceDate; // 출석 일자

  @Column(nullable = false)
  private LocalDateTime checkInTime; // 등원 시간

  @Column(nullable = false)
  private LocalDateTime checkOutTime; // 하원 시간

  @Column(length = 100, nullable = false)
  private String attendanceStatus;   // 출석 상태 (출석, 지각, 결석 등)

  /*
  Hibernate:
    create table children_attendance (
        attendance_id bigint not null auto_increment,
        attendance_date date not null,
        attendance_status varchar(100) not null,
        check_in_time datetime(6) not null,
        check_out_time datetime(6) not null,
        children_id bigint not null,
        primary key (attendance_id)
    ) engine=InnoDB

    Hibernate:
    alter table if exists children_attendance
       add constraint FKiqfgv236l3dgrm1llmu08outw
       foreign key (children_id)
       references children (children_id)
   */

}