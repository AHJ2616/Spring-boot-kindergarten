package com.kinder.kindergarten.DTO.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO { //Firebase 로 요청하는 값

    private String targetToken;    // 유저의 기기 토큰
    private String title;          // 알림 제목
    private String body;           // 알림 내용
    private String image;          // 알림 이미지 URL
    private String clickAction;    // 클릭 시 이동할 URL
    private String type;           // 알림 타입 (ANDROID, IOS, WEB)
}
