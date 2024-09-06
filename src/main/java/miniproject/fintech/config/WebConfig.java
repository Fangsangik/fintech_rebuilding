package miniproject.fintech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
    Post 요청을 DELETE로 변환 -> HiddenHttpMethodFilter를 설정해야 함
    HTML 폼에서 Post 요청을 DELET등 HTTP 메소드로 변환
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }

    //외부 도메인(예: 다른 웹사이트에서)에서 오는 경우, CORS 설정이 필요할 수 있디.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://allowed-origin.com")
                .allowedMethods("POST", "GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        // /member/** 경로에 대한 CORS 설정 추가
        registry.addMapping("/member/**")
                .allowedOrigins("http://allowed-origin.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/api/refresh-token")
                .allowedOrigins("http://allowed-origin.com")
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    //Spring Security의 StrictHttpFirewall이 요청 URL에 포함된 잠재적으로 악의적인 문자열 %0A
    // (URL 인코딩된 줄 바꿈 문자)을 감지하고 해당 요청을 차단
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true); // % 문자를 허용
        firewall.setAllowUrlEncodedSlash(true); // 슬래시(/) 문자를 허용
        firewall.setAllowUrlEncodedDoubleSlash(true); // 이중 슬래시(//) 허용
        firewall.setAllowBackSlash(true); // 역슬래시(\) 허용
        firewall.setAllowSemicolon(true); // 세미콜론(;) 허용
        firewall.setAllowUrlEncodedPeriod(true); // 점(.) 허용
        return firewall;
    }

}
