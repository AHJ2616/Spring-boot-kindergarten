package com.kinder.kindergarten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.List;
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

/*    registry.addResourceHandler("/upload/**")
            .addResourceLocations(uploadPath2);

    registry.addResourceHandler("/images/**") //웹 브라우저에 입력하는 url에 /images로 시작하는 경우 uploadPath에 폴더에서 파일을 읽어오도록 설정한다.
            .addResourceLocations(uploadPath2);  //로컬 컴퓨터에 저장된 파일을 읽어올 root 경로 지정

    // Summernote 이미지 경로 추가
    registry.addResourceHandler("/images/summernote/**")
            .addResourceLocations(uploadPath2 + "summernote/");
    
      // No static resource css/bootstrap.min.css.map 오류 해결
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");*/
    // 기본 이미지 리소스 핸들러
    registry.addResourceHandler("/images/**")
            .addResourceLocations(uploadPath2)
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()     // 캐시 만료시 서버에 재검증
                    .cachePublic())       // 공용 캐시 허용
            .resourceChain(true)
            .addResolver(new VersionResourceResolver()
                    .addContentVersionStrategy("/**"))
            .addResolver(new PathResourceResolver());  // 원본 리소스 조회 가능하도록 설정

    // 썸네일 이미지 리소스 핸들러
    registry.addResourceHandler("/images/thumbnails/**")
            .addResourceLocations(uploadPath2 + "thumbnails/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()
                    .cachePublic())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());

    // 중간 크기 이미지 리소스 핸들러
    registry.addResourceHandler("/images/medium/**")
            .addResourceLocations(uploadPath2 + "medium/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()
                    .cachePublic())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());

    // Summernote 이미지 리소스 핸들러
    registry.addResourceHandler("/images/summernote/**")
            .addResourceLocations(uploadPath2 + "summernote/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()
                    .cachePublic())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());

    // 일반 업로드 파일 리소스 핸들러
    registry.addResourceHandler("/upload/**")
            .addResourceLocations(uploadPath2)
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()
                    .cachePublic())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());

    // 정적 리소스 핸들러
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)
                    .mustRevalidate()
                    .cachePublic())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());


    registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/");

    // Firebase 서비스 워커 파일 설정
    registry.addResourceHandler("/firebase-messaging-sw.js")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.noCache())
            .resourceChain(true)
            .addResolver(new PathResourceResolver());

    // Firebase 클라우드 메시징 스코프 설정
    registry.addResourceHandler("/firebase-cloud-messaging-push-scope/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.noCache());

  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new CustomAuthenticationPrincipalArgumentResolver());
  }

}
