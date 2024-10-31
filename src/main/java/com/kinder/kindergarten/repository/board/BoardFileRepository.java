package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.entity.board.BoardEntity;
import com.kinder.kindergarten.entity.board.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFileEntity, String> {
  List<BoardFileEntity> findByBoardEntity(BoardEntity boardEntity);
  List<BoardFileEntity> findByBoardEntity_BoardId(String boardId);

}
