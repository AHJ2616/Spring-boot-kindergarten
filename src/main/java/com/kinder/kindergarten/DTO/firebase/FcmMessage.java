package com.kinder.kindergarten.DTO.firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage { //FIrebase에서 넘어온 값

  private boolean validateOnly;
  private Message message;

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Message {
    private Notification notification;
    private String token;
    private String topic;
    private Data data;
    private AndroidConfig android;
    private ApnsConfig apns;
    private WebpushConfig webpush;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Notification {
    private String title;
    private String body;
    private String image;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Data {
    private String clickAction;
    private String id;
    private String type;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class AndroidConfig {
    private String priority;
    private String channelId;
    private boolean directBootOk;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class ApnsConfig {
    private Aps aps;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class Aps {
    private String sound;
    private String badge;
    private boolean contentAvailable;
    private String category;
    private boolean mutableContent;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class WebpushConfig {
    private WebNotification notification;
    private FcmOptions fcmOptions;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class WebNotification {
    private String icon;
    private String badge;
    private String[] actions;
  }

  @Builder
  @AllArgsConstructor
  @Getter
  public static class FcmOptions {
    private String link;
  }
}
