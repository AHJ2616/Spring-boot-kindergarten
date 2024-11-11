package com.kinder.kindergarten.DTO.firebase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {
  private String deviceToken;
  private String title;
  private String body;
}
