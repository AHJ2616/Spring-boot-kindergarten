package com.kinder.kindergarten.controller.board;

import com.kinder.kindergarten.DTO.board.CommentsDTO;
import com.kinder.kindergarten.annotation.CurrentUser;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.board.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Log4j2
public class CommentsRestController {

  private final CommentsService commentsService;

  @PostMapping("/write")
  public ResponseEntity<CommentsDTO> createComment(@RequestBody CommentsDTO commentsDTO, @CurrentUser PrincipalDetails PrincipalDetails) {
    log.info("댓글작성 : " +commentsDTO);
    CommentsDTO savedComment = commentsService.createComment(commentsDTO,PrincipalDetails.getUsername());//authentication.getName()
    return ResponseEntity.ok(savedComment);
  }

  @DeleteMapping("/delete/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable String commentId,
  @CurrentUser PrincipalDetails PrincipalDetails) {
    commentsService.deleteComment(commentId, PrincipalDetails.getUsername());
    return ResponseEntity.ok().build();
  }

  @PutMapping("/update/{commentId}")
  public ResponseEntity<CommentsDTO> updateComment(@PathVariable String commentId,
                                                   @RequestBody CommentsDTO commentsDTO,
                                                   @CurrentUser PrincipalDetails PrincipalDetails) {
    CommentsDTO updatedComment = commentsService.updateComment(commentId, commentsDTO, PrincipalDetails.getUsername());
    return ResponseEntity.ok(updatedComment);
  }
}
