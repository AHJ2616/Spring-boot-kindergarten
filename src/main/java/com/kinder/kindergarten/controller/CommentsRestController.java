package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.board.CommentsDTO;
import com.kinder.kindergarten.service.board.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsRestController {

  private final CommentsService commentsService;

  @PostMapping("/write")
  public ResponseEntity<CommentsDTO> createComment(@RequestBody CommentsDTO commentsDTO,
                                                   Authentication authentication) {
    CommentsDTO savedComment = commentsService.createComment(commentsDTO, authentication.getName());
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
