package miniproject.fintech.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.service.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = extractJwtFromRequest(request);
        String requestURI = request.getRequestURI();

        if ("/api/login".equals(requestURI) || "/api/refresh-token".equals(requestURI) || "/register/create".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtToken != null && jwtTokenUtil.validateToken(jwtToken)) {
                String userId = jwtTokenUtil.getUsernameFromToken(jwtToken);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwtToken == null) {
                log.warn("Authorization header missing");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Authorization header missing");
                return;
            }
        } catch (ExpiredJwtException ex) {
            log.warn("JWT 토큰이 만료되었습니다. 만료된 토큰을 통해 새 토큰을 발급받을 수 있습니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("JWT 토큰이 만료되었습니다. 새 토큰을 발급받으세요.");
            return;
        } catch (UsernameNotFoundException ex) {
            log.error("사용자를 찾을 수 없습니다: {}", ex.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("사용자를 찾을 수 없습니다: " + ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
