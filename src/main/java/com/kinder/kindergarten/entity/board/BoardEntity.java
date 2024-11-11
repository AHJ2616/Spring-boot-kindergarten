package com.kinder.kindergarten.entity.board;

import com.kinder.kindergarten.constant.board.BoardType;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter  @Setter
@ToString
@DynamicInsert //insert시 null인 필드는 제외하기 ( @ColumnDefault 적용)
public class BoardEntity extends TimeEntity {


  @Id
  @Column(name = "board_id")
  private String boardId;

  @Column(nullable = false)
  private String boardTitle;

  @Column(nullable = false,columnDefinition = "TEXT")
  @Lob
  private String boardContents;

  @Enumerated(EnumType.STRING)
  @Column(name = "board_type")
  private BoardType boardType;

  @Column(nullable = false) //나중에 변경
  private String boardWriter;

  @ColumnDefault("0")
  private Integer views;

  @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BoardFileEntity> boardFiles = new ArrayList<>();



}
