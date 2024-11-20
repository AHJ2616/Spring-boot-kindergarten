package com.kinder.kindergarten.entity.board;

import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "comments")
@Getter  @Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class CommentsEntity extends TimeEntity {

  @Id
  @Column(name="comments_id")
  private String commentsId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="member_id", referencedColumnName = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="board_id")
  private BoardEntity boardId;

  @Column(nullable = false)
  private String contents;

}
