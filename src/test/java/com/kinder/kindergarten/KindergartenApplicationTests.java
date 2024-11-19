package com.kinder.kindergarten;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.service.MemberService;

import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class KindergartenApplicationTests {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트")
    void saveMemberTest() {
        // given
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail("test@mbc.com");
        memberDTO.setPassword("123");
        memberDTO.setName("테스트");
        memberDTO.setPhone("010-1234-5678");
        memberDTO.setAddress("수원");
        memberDTO.setRole("ROLE_USER");

        // when
        memberService.saveMember(memberDTO);
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Web Push 기능 테스트")
    void testWebPush() throws IOException, FirebaseMessagingException {
        // Firebase 메시지 생성
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("제목")
                        .setBody("내용")
                        .build())
                .setToken("your-device-token") // 실제 디바이스 토큰으로 변경 필요
                .build();

        // Firebase 메시지 전송
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Firebase 메시지 전송 응답: " + response);

        // OkHttp 클라이언트 생성
        OkHttpClient client = new OkHttpClient();

        // JSON 데이터 생성
        Gson gson = new Gson();
        String jsonData = gson.toJson(message);

        // HTTP 요청 생성
        RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("localhost/board/list/common") // 실제 엔드포인트로 변경 필요
                .post(body)
                .build();

        // HTTP 요청 전송
        try (Response httpResponse = client.newCall(request).execute()) {
            if (!httpResponse.isSuccessful()) throw new IOException("Unexpected code " + httpResponse);

            System.out.println("HTTP 요청 응답: " + httpResponse.body().string());
        }
    }
}
