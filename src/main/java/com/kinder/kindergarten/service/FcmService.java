package com.kinder.kindergarten.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.kinder.kindergarten.DTO.firebase.FcmMessage;
import com.kinder.kindergarten.DTO.firebase.RequestDTO;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {
    private static final String API_URL = "https://fcm.googleapis.com/v1/projects/{PROJECT_ID}/messages:send";
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private final String PROJECT_ID = "kinder-60299"; // Firebase 프로젝트 ID로 변경
    
    private final Gson gson;

    // 플랫폼별 메시지 생성
    public FcmMessage createMessage(RequestDTO requestDTO) {
        String type = requestDTO.getType().toUpperCase();
        
        FcmMessage.Message.MessageBuilder messageBuilder = FcmMessage.Message.builder()
                .token(requestDTO.getTargetToken())
                .notification(FcmMessage.Notification.builder()
                        .title(requestDTO.getTitle())
                        .body(requestDTO.getBody())
                        .image(requestDTO.getImage())
                        .build());

        switch (type) {
            case "ANDROID":
                messageBuilder.android(FcmMessage.AndroidConfig.builder()
                        .priority("high")
                        .channelId("default")
                        .directBootOk(true)
                        .build());
                break;
                
            case "IOS":
                messageBuilder.apns(FcmMessage.ApnsConfig.builder()
                        .aps(FcmMessage.Aps.builder()
                                .sound("default")
                                .badge("1")
                                .contentAvailable(true)
                                .category("NEW_MESSAGE")
                                .mutableContent(true)
                                .build())
                        .build());
                break;
                
            case "WEB":
                messageBuilder.webpush(FcmMessage.WebpushConfig.builder()
                        .notification(FcmMessage.WebNotification.builder()
                                .icon("/images/icon.png")
                                .badge("/images/badge.png")
                                .actions(new String[]{"확인", "닫기"})
                                .build())
                        .fcmOptions(FcmMessage.FcmOptions.builder()
                                .link(requestDTO.getClickAction())
                                .build())
                        .build());
                break;
        }

        return FcmMessage.builder()
                .validateOnly(false)
                .message(messageBuilder.build())
                .build();
    }

    // FCM 메시지 전송
    public void sendMessage(RequestDTO requestDTO) throws IOException {
        String message = gson.toJson(createMessage(requestDTO));
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        
        Request request = new Request.Builder()
                .url(API_URL.replace("{PROJECT_ID}", PROJECT_ID))
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + getAccessToken())
                .addHeader("Content-Type", "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        
        if (!response.isSuccessful()) {
            throw new IOException("FCM 메시지 전송 실패: " + response.body().string());
        }
    }

    // Firebase 액세스 토큰 얻기
    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("serviceAccountKey.json").getInputStream())
                .createScoped(List.of(MESSAGING_SCOPE));
        
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
