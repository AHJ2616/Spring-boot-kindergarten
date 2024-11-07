package com.kinder.kindergarten.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kinder.kindergarten.DTO.board.BoardDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

  @Bean
  public LoadingCache<String, Page<BoardDTO>> searchCache() {
    return CacheBuilder.newBuilder()
            .maximumSize(500) // 최대 100개의 검색 결과를 캐시
            .expireAfterWrite(30, TimeUnit.MINUTES) // 30분 후 캐시 만료
            .build(new CacheLoader<>() {
              @Override
              public Page<BoardDTO> load(String key) {
                return null; // 실제 검색은 서비스에서 수행
              }
            });
  }

}
