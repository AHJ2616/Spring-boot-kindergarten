package com.kinder.kindergarten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  //WebMvcConfigurer 인터페이스의 구현체

  @Value("${uploadPath1}") //application.properties에 설정한 "uploadPath" 값을 읽어온다.
  String uploadPath1;

  @Value("${uploadPath2}") //application.properties에 설정한 "uploadPath" 값을 읽어온다.
  String uploadPath2;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry){

    registry.addResourceHandler("/upload/**")
            .addResourceLocations(uploadPath2);

    registry.addResourceHandler("/images/**") //웹 브라우저에 입력하는 url에 /images로 시작하는 경우 uploadPath에 폴더에서 파일을 읽어오도록 설정한다.
            .addResourceLocations(uploadPath2)  //로컬 컴퓨터에 저장된 파일을 읽어올 root 경로 지정
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)) // 브라우저 캐싱 1시간 설정
            .resourceChain(true) // 리소스 체인 활성화
            .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**")) // 버전 기반 캐싱
            .addTransformer(new CssLinkResourceTransformer()); // CSS 최적화

    // Summernote 이미지 경로 추가
    registry.addResourceHandler("/images/summernote/**")
            .addResourceLocations(uploadPath2 + "summernote/");

    // No static resource css/bootstrap.min.css.map 오류 해결
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");

    // 썸네일 이미지용 경로 추가
    registry.addResourceHandler("/images/thumbnails/**")
            .addResourceLocations(uploadPath2 + "thumbnails/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS));

    registry.addResourceHandler("/images/medium/**")
            .addResourceLocations(uploadPath2 + "medium/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS));

  }
}
