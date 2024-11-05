package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.board.CommentsDTO;
import com.kinder.kindergarten.service.board.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Log4j2
public class CommentsRestController {

  private final CommentsService commentsService;

  @PostMapping("/write")
  public ResponseEntity<CommentsDTO> createComment(@RequestBody CommentsDTO commentsDTO,
                                                   Authentication authentication) {
    log.info("댓글작성 : " +commentsDTO);
    CommentsDTO savedComment = commentsService.createComment(commentsDTO,"테스트답변가");//authentication.getName()
    return ResponseEntity.ok(savedComment);
  }

  @DeleteMapping("/delete/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable String commentId,
                                            Authentication authentication) {
    commentsService.deleteComment(commentId, authentication.getName());
    return ResponseEntity.ok().build();
  }

  @PutMapping("/update/{commentId}")
  public ResponseEntity<CommentsDTO> updateComment(@PathVariable String commentId,
                                                   @RequestBody CommentsDTO commentsDTO,
                                                   Authentication authentication) {
    CommentsDTO updatedComment = commentsService.updateComment(commentId, commentsDTO, authentication.getName());
    return ResponseEntity.ok(updatedComment);
  }
}
