package miniproject.fintech.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching // 캐시 기능을 활성화
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() { //캐시를 관리하는 역할
        return new ConcurrentMapCacheManager("fintechCache"); //기본 메모리 기반 캐시 (heap 메모리 ㅈ당)
    }
}
