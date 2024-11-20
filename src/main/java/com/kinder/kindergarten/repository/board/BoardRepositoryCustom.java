package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.constant.board.BoardType;
import com.kinder.kindergarten.entity.board.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {
    Page<BoardEntity> findByKeyword(String keyword, Pageable pageable);
    Page<BoardEntity> searchBoards(BoardType boardType, String keyword, Pageable pageable);
    Page<BoardEntity> findByBoardTypeWithFiles(BoardType boardType, Pageable pageable);
} 