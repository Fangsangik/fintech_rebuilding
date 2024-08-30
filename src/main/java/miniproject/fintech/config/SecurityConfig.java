package miniproject.fintech.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible URLs
                        .requestMatchers("/login**", "/home**", "/register/create").permitAll()

                        // URLs accessible by ADMIN and USER roles
                        .requestMatchers("/process", "/transaction/**", "/deposit/**", "/member/update/**", "/member/delete/**", "/account/**")
                        .hasAnyRole("ADMIN", "USER")

                        // URLs accessible by ADMIN role only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")  // 로그인 페이지 설정
                        .loginProcessingUrl("/perform_login")  // 로그인 폼의 action URL
                        .defaultSuccessUrl("/home", true)  // 로그인 성공 시 리디렉션할 URL
                        .failureUrl("/login?error=true")  // 로그인 실패 시 리디렉션할 URL
                        .permitAll()  // 로그인 페이지는 누구나 접근 가능
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // 로그아웃 URL
                        .logoutSuccessUrl("/login?logout=true")  // 로그아웃 성공 시 리디렉션할 URL
                        .permitAll()  // 로그아웃 URL은 누구나 접근 가능
                );

        return http.build();
    }
}
