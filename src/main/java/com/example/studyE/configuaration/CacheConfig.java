package com.example.studyE.configuaration;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager mgr = new CaffeineCacheManager(
                "dictCache",
                "translateCache"
        );
        mgr.setCaffeine(Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(Duration.ofDays(7)));
        return mgr;
    }
}
