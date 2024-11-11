package com.kinder.kindergarten.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class FcmService {
  private final String API_URL = "https://fcm.googleapis.com/v1/projects/kinder-60299/messages:send";
  @Value("${firebase.server.key}")
  private String firebaseServerKey;

  private final OkHttpClient httpClient;
  private final Gson gson;

  public FcmService() {
    this.httpClient = new OkHttpClient();
    this.gson = new Gson();
  }

  public void sendNotification(String targetToken, String title, String body) throws IOException {
    // 알림 데이터 작성
    Map<String, Object> message = new HashMap<>();
    message.put("to", targetToken);

    Map<String, String> notification = new HashMap<>();
    notification.put("title", title);
    notification.put("body", body);

    message.put("notification", notification);

    // JSON 변환
    String jsonMessage = gson.toJson(message);

    // 요청 생성
    RequestBody requestBody = RequestBody.create(jsonMessage, MediaType.get("application/json; charset=utf-8"));
    Request request = new Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .addHeader("Authorization", "key=" + firebaseServerKey)
            .build();

    // 요청 전송
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Failed to send notification: " + response);
      }
      System.out.println("Notification sent successfully: " + response.body().string());
    }
  }

}
