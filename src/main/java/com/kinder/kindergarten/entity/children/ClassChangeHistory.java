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
  private LocalDate classChangeDate; // 반 변경 일자

  /*

  Hibernate:
    create table class_change_history (
        history_id bigint not null auto_increment,
        class_change_date date not null,
        children_id bigint not null,
        new_class_id bigint not null,
        previous_class_id bigint,
        primary key (history_id)
    ) engine=InnoDB

    Hibernate:
    alter table if exists class_change_history
       add constraint FK2i94p3j490ccx22e5mmvshgjg
       foreign key (children_id)
       references children (children_id)
Hibernate:
    alter table if exists class_change_history
       add constraint FKpxpkq4rjxp4487n0m4gib7xp9
       foreign key (new_class_id)
       references class_room (class_room_id)
Hibernate:
    alter table if exists class_change_history
       add constraint FKl94bg0xoalu59rwl87c72buyb
       foreign key (previous_class_id)
       references class_room (class_room_id)
   */

}