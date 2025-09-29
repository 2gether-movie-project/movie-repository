package com.movieproject.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "directorDetailCache",  // 여기에 사용할 캐시 이름 넣으면 됩니다
                "actorDetailCache",
                "searchTitleCache",
                "searchActorCache",
                "searchDirectorCache",
                "popularSearchCache",
                "movieDetailsCache"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // TTL
                .maximumSize(1000) // 캐시 최대 크기
        );
        return cacheManager;
    }
}
