package com.kinder.kindergarten.controller.board;

import com.kinder.kindergarten.DTO.board.CommentsDTO;
import com.kinder.kindergarten.annotation.CurrentUser;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.board.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Log4j2
public class CommentsRestController {

  private final CommentsService commentsService;

  @PostMapping
  public ResponseEntity<CommentsDTO> createComment(
          @RequestBody CommentsDTO commentsDTO,
          @CurrentUser PrincipalDetails principalDetails) {
    try {
      log.info("댓글 작성 요청: {}", commentsDTO);
      CommentsDTO savedComment = commentsService.createComment(
          commentsDTO,
          principalDetails.getMember().getId()
      );
      return ResponseEntity.ok(savedComment);
    } catch (Exception e) {
      log.error("댓글 작성 중 오류 발생", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/delete/{commentId}")
  public ResponseEntity<Void> deleteComment(
          @PathVariable String commentId,
          @CurrentUser PrincipalDetails principalDetails) {
    try {
      commentsService.deleteComment(commentId, principalDetails.getMember().getId());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("댓글 삭제 중 오류 발생", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/update/{commentId}")
  public ResponseEntity<CommentsDTO> updateComment(
          @PathVariable String commentId,
          @RequestBody CommentsDTO commentsDTO,
          @CurrentUser PrincipalDetails principalDetails) {
    try {
      CommentsDTO updatedComment = commentsService.updateComment(
          commentId,
          commentsDTO,
          principalDetails.getMember().getId()
      );
      return ResponseEntity.ok(updatedComment);
    } catch (Exception e) {
      log.error("댓글 수정 중 오류 발생", e);
      return ResponseEntity.badRequest().build();
    }
  }
}
