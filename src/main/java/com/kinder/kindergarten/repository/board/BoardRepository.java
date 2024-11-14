package com.kinder.kindergarten.repository.board;

import com.kinder.kindergarten.constant.board.BoardType;
import com.kinder.kindergarten.entity.board.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, String> {

  //게시판 타입별로 전체 불러오기
  Page<BoardEntity> findByBoardType(BoardType boardType, Pageable pageable);

  // 기본 검색 메소드 수정
  @Query("SELECT b FROM BoardEntity b JOIN b.member m " +
         "WHERE b.boardTitle LIKE %:keyword% " +
         "OR b.boardContents LIKE %:keyword% " +
         "OR m.name LIKE %:keyword%")
  Page<BoardEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

  //페이지 삭제
  void deleteByBoardId(String boardId);

  // 게시판 타입별 검색
  @Query("SELECT b FROM BoardEntity b " +
         "JOIN b.member m " +
         "WHERE b.boardType = :boardType " +
         "AND (b.boardTitle LIKE %:keyword% " +
         "OR b.boardContents LIKE %:keyword% " +
         "OR m.name LIKE %:keyword%)")
  Page<BoardEntity> searchBoards(
      @Param("boardType") BoardType boardType,
      @Param("keyword") String keyword,
      Pageable pageable
  );

  // 게시판 타입과 제목으로 검색
  Page<BoardEntity> findByBoardTypeInAndBoardTitleContaining(
      List<BoardType> types, 
      String keyword, 
      Pageable pageable
  );

  // 게시판 타입으로 검색
  Page<BoardEntity> findByBoardTypeIn(
      List<BoardType> types, 
      Pageable pageable
  );

  // 게시글과 첨부파일 함께 조회
  @Query("SELECT DISTINCT b FROM BoardEntity b " +
         "LEFT JOIN FETCH b.boardFiles " +
         "LEFT JOIN FETCH b.member " +
         "WHERE b.boardType = :boardType " +
         "ORDER BY b.regiDate DESC")
  Page<BoardEntity> findByBoardTypeWithFiles(
      @Param("boardType") BoardType boardType, 
      Pageable pageable
  );

  List<BoardEntity> findByBoardType(BoardType boardType);

}
