package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value="/api/notifications")
@RequiredArgsConstructor
public class FcmRestController {

  private final FcmService fcmService;

  @PostMapping(value="/send")
  public String sendNotification(@RequestParam String targetToken, @RequestParam String title,@RequestParam String body){
    try {
      fcmService.sendNotification(targetToken, title, body);
      return "Notification sent successfully";
    } catch (IOException e) {
      e.printStackTrace();
      return "Failed to send notification";
    }
  }
}
