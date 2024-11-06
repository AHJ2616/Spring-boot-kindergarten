package com.kinder.kindergarten.controller.board;

import com.kinder.kindergarten.DTO.board.BoardDTO;
import com.kinder.kindergarten.constant.BoardType;
import com.kinder.kindergarten.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/board")
public class BoardRestController {

  private final BoardService boardService;

  @GetMapping("/sort/{type}") //정렬
  public ResponseEntity<Page<BoardDTO>> getSortedBoards(@PathVariable String type,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam String sortBy) {
    try {
      BoardType boardType = BoardType.valueOf(type.toUpperCase(Locale.ROOT));
      Sort sort;
      switch (sortBy) {
        case "regiDate":
          sort = Sort.by(Sort.Direction.DESC, "regiDate");
          break;
        case "views":
          sort = Sort.by(Sort.Direction.DESC, "views");
          break;
        case "likes":
          sort = Sort.by(Sort.Direction.DESC, "likes");
          break;
        default:
          sort = Sort.by(Sort.Direction.DESC, "regiDate");
      }

      Pageable pageable = PageRequest.of(page, 10, sort);
      Page<BoardDTO> boards = boardService.getBoardsByType(boardType,pageable);
      return ResponseEntity.ok(boards);

      // 처리 로직
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid board type");
    }



  }

  @PostMapping(value="/uploadImage", produces = "application/json")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file)throws IllegalStateException, IOException {
    try {
      String imageUrl = boardService.uploadSummernoteImage(file);
      Map<String, String> response = new HashMap<>();
      response.put("url", imageUrl);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/search")
  public ResponseEntity<Page<BoardDTO>> searchAndSortBoards(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(required = false) String sortBy) {

    Sort sort;
    if (sortBy != null) {
      sort = switch (sortBy) {
        case "regiDate" -> Sort.by(Sort.Direction.DESC, "regiDate");
        case "views" -> Sort.by(Sort.Direction.DESC, "views");
        case "likes" -> Sort.by(Sort.Direction.DESC, "likes");
        default -> Sort.by(Sort.Direction.DESC, "regiDate");
      };
    } else {
      sort = Sort.by(Sort.Direction.DESC, "regiDate");
    }

    Pageable pageable = PageRequest.of(page, 10, sort);
    Page<BoardDTO> searchResults = boardService.searchBoards(keyword, pageable);

    return ResponseEntity.ok(searchResults);
  }

}
