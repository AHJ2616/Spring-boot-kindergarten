package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.entity.board.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity,String> {

    List<CommentsEntity> findByBoardId_BoardIdOrderByRegiDateDesc(String boardId);
}
