package com.kinder.kindergarten.controller.board;

import com.kinder.kindergarten.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/board")
public class BoardRestController {

  private final BoardService boardService;



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

}