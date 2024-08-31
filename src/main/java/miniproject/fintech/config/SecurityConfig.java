package miniproject.fintech.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF 보호 비활성화 (API 테스트용)
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible URLs
                        .requestMatchers("/api/login", "/register/create").permitAll()  // 로그인과 회원가입은 누구나 접근 가능
                        .requestMatchers("/process", "/transaction/**", "/deposit/**", "/member/**", "/account/**").permitAll()
                        //.hasAnyRole("ADMIN", "USER")  // 특정 URL은 ADMIN과 USER만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // ADMIN만 접근 가능
                        .anyRequest().authenticated()  // 그 외의 모든 요청은 인증 필요
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 또는 다른 토큰 기반 인증 사용 시 Stateless로 설정
                .httpBasic(Customizer.withDefaults()); // 기본적인 HTTP Basic 인증 사용

        return http.build();
    }
}
