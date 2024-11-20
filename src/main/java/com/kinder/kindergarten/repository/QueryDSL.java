package com.kinder.kindergarten.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kinder.kindergarten.entity.board.QBoardEntity;
import com.kinder.kindergarten.repository.board.BoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class QueryDSL {

  private final JPAQueryFactory queryFactory;
  private BoardRepository boardRepository;

  //Guava CacheBuilder 사용하기
  private final Cache<String, LocalDateTime> viewLogCache = CacheBuilder.newBuilder()
          .expireAfterWrite(6, TimeUnit.HOURS) // 6시간 동안 캐시에 저장
          .build();

  // IP당 조회수 증가 제한 시간 (6시간)
  private static final long VIEW_LIMIT_HOURS = 6;

  @Transactional
  public void increaseViews(String boardId,HttpServletRequest request) {
    String ip = getClientIP(request);
    String mapKey = ip + ":" + boardId;

    LocalDateTime lastViewTime = viewLogCache.getIfPresent(mapKey);
    LocalDateTime now = LocalDateTime.now();

    if (lastViewTime == null || lastViewTime.plusHours(VIEW_LIMIT_HOURS).isBefore(now)) {
      // 조회수 증가
      QBoardEntity board = QBoardEntity.boardEntity;
      queryFactory
              .update(board)
              .set(board.views, board.views.add(1))
              .where(board.boardId.eq(boardId))
              .execute();
      viewLogCache.put(mapKey, now); // IP 저장
    }
  }
  // 클라이언트 IP 주소 가져오기
  private String getClientIP(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");

    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }


}//class end
