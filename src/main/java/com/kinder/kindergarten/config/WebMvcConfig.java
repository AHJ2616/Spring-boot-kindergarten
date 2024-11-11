package com.kinder.kindergarten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${uploadPath1}")
  String uploadPath1;

  @Value("${uploadPath2}")
  String uploadPath2;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
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
  }
}
