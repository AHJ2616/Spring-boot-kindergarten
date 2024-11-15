package com.kinder.kindergarten.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Log4j2
public class FirebaseConfig {

  @PostConstruct
  public void initialize() {
    try {
      ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
      FileInputStream serviceAccount = new FileInputStream(resource.getFile());

      FirebaseOptions options = FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
      }
    } catch (IOException e) {
      log.error("Firebase 초기화 중 오류 발생: ", e);
    }
  }

}
