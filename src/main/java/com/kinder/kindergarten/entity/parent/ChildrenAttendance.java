package com.kinder.kindergarten.entity.parent;

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
  @JoinColumn(name = "parentId", nullable = false)
  private Parent children;

  @Column(nullable = false)
  private LocalDate attendanceDate; // 출석 일자

  @Column(nullable = false)
  private LocalDateTime checkInTime; // 등원 시간

  private LocalDateTime checkOutTime; // 하원 시간

  @Column(length = 100)
  private String attendanceStatus;   // 출석 상태 (출석, 지각, 결석 등)

    /*

    Hibernate:
    create table children_attendance (
        cd_attend_id bigint not null auto_increment,
        cd_attend_date date not null,
        cd_attend_status varchar(255) not null,
        children_id bigint not null,
        primary key (cd_attend_id)
    ) engine=InnoDB

11.01 추가
Hibernate:
    alter table if exists children_attendance
       add column attendance_date date not null
Hibernate:
    alter table if exists children_attendance
       add column attendance_status varchar(100)
Hibernate:
    alter table if exists children_attendance
       add column check_in_time datetime(6) not null
Hibernate:
    alter table if exists children_attendance
       add column check_out_time datetime(6)
Hibernate:
    alter table if exists children_attendance
       add column parent_id bigint not null
Hibernate:
    alter table if exists parent
       modify column parent_birth_date date not null
Hibernate:
    alter table if exists children_attendance
       add constraint FKjpsf4x0i4wodod8aoii1isnth
       foreign key (parent_id)
       references parent (parent_id)
     */
}