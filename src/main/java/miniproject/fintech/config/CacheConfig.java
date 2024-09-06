package miniproject.fintech.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;

@EnableCaching // 캐시 기능을 활성화
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // 기본 메모리 내 캐시 매니저를 사용하여 "defaultCache"라는 이름의 캐시를 생성
        return new ConcurrentMapCacheManager("MemberCache", "depositCache", "adminCache", "accountsCache", "transfersCache", "transactionCache", "register");
    }
}
