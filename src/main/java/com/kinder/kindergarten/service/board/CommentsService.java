package com.kinder.kindergarten.service.board;

import com.kinder.kindergarten.DTO.board.CommentsDTO;
import com.kinder.kindergarten.entity.board.BoardEntity;
import com.kinder.kindergarten.entity.board.CommentsEntity;
import com.kinder.kindergarten.repository.board.BoardRepository;
import com.kinder.kindergarten.repository.board.CommentsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentsService {
  private final CommentsRepository commentsRepository;
  private final BoardRepository boardRepository;

  private final ModelMapper modelMapper;

  public CommentsDTO createComment(CommentsDTO commentsDTO, String writer) {
    BoardEntity board = boardRepository.findById(commentsDTO.getBoardId())
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

    CommentsEntity comment = new CommentsEntity();
    comment.setBoardId(board);
    comment.setWriter(writer);
    comment.setContents(commentsDTO.getContents());

    CommentsEntity savedComment = commentsRepository.save(comment);
    return convertToDTO(savedComment);
  }

  public List<CommentsDTO> getCommentsByBoardId(String boardId) {
    return commentsRepository.findByBoardId_BoardIdOrderByRegiDateDesc(boardId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
  }

  public void deleteComment(String commentId, String writer) {
    CommentsEntity comment = commentsRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

    if (!comment.getWriter().equals(writer)) {
      throw new RuntimeException("댓글 삭제 권한이 없습니다.");
    }

    commentsRepository.delete(comment);
  }

  public CommentsDTO updateComment(String commentId, CommentsDTO commentsDTO, String writer) {
    CommentsEntity comment = commentsRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

    if (!comment.getWriter().equals(writer)) {
      throw new RuntimeException("댓글 수정 권한이 없습니다.");
    }

    comment.setContents(commentsDTO.getContents());

    CommentsEntity updatedComment = commentsRepository.save(comment);
    return convertToDTO(updatedComment);
  }

  private CommentsDTO convertToDTO(CommentsEntity entity) {
    return modelMapper.map(entity, CommentsDTO.class);
  }
}
