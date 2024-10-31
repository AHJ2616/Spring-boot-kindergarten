package com.kinder.kindergarten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  //WebMvcConfigurer 인터페이스의 구현체

  @Value("${uploadPath1}") //application.properties에 설정한 "uploadPath" 값을 읽어온다.
  String uploadPath;

  @Value("${uploadPath2}") //application.properties에 설정한 "uploadPath" 값을 읽어온다.
  String uploadPath2;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry){
    registry.addResourceHandler("/images/**") //웹 브라우저에 입력하는 url에 /images로 시작하는 경우 uploadPath에 폴더에서 파일을 읽어오도록 설정한다.
            .addResourceLocations(uploadPath);  //로컬 컴퓨터에 저장된 파일을 읽어올 root 경로 지정

    // Summernote 이미지 경로 추가
    registry.addResourceHandler("/images/summernote/**")
            .addResourceLocations("file:" + uploadPath2 + "/summernote/");

  }
}
