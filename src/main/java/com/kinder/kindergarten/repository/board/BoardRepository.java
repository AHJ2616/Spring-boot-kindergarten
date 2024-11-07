package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.constant.BoardType;
import com.kinder.kindergarten.entity.board.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity,String> {

  //게시판 타입별로 전체 불러오기
  Page<BoardEntity> findByBoardType(BoardType boardType, Pageable pageable);

  // 검색을 위한 메소드 추가
  Page<BoardEntity> findByBoardTitleContainingOrBoardContentsContainingOrBoardWriterContaining(
          String title, String content, String writer, Pageable pageable);

  //페이지 삭제
  void deleteByBoardId(String boardId);

  Page<BoardEntity> findByBoardTypeAndBoardTitleContainingOrBoardTypeAndBoardContentsContainingOrBoardTypeAndBoardWriterContaining(
          BoardType boardType1, String keyword1,
          BoardType boardType2, String keyword2,
          BoardType boardType3, String keyword3,
          Pageable pageable
  );

  List<BoardEntity> findByBoardType(BoardType boardType);

  Page<BoardEntity> findByBoardTypeAndBoardTitleContainingOrBoardContentsContaining(
          BoardType boardType, String titleKeyword, String contentsKeyword, Pageable pageable);

}
