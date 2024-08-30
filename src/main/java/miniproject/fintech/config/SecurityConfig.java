package miniproject.fintech.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig { // 클래스 이름 변경

    // BCryptPasswordEncoder를 빈으로 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible URLs
                        .requestMatchers("/login**", "/home**", "/member/create").permitAll()

                        // URLs accessible by ADMIN and USER roles
                        .requestMatchers("/process", "/transaction/**", "/deposit/**", "/member/update/**", "/member/delete/**", "/account/**")
                        .hasAnyRole("ADMIN", "USER")

                        // URLs accessible by ADMIN role only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
