package miniproject.fintech.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final long REFRESH_TOKEN_VALIDITY = 604800000; // 7일 (밀리초 단위)

    // JWT 토큰에서 사용자 이름을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // JWT 토큰에서 만료 날짜를 추출하는 메서드
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // JWT 토큰에서 특정 클레임을 추출하는 메서드
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임을 추출하는 메서드
    private Claims getAllClaimsFromToken(String token) {
        try {

            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(60) // 클럭 스큐 허용 (60초)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 토큰");
        }
    }

        // JWT 토큰이 만료되었는지 확인하는 메서드
        private Boolean isTokenExpired (String token){
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        }

        // 사용자 이름을 기반으로 JWT 토큰을 생성하는 메서드
        public String generateToken (String userId){
            return Jwts.builder()
                    .setSubject(userId)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)) // 만료 시간 설정
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
        }

        // 사용자 이름을 기반으로 리프레시 토큰을 생성하는 메서드
        public String generateRefreshToken (String userId){
            return Jwts.builder()
                    .setSubject(userId)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
        }

        public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();

            claims.put("roles", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis()))
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
        }

        // JWT 토큰의 유효성을 검사하는 메서드
        public Boolean validateToken(String token) {
            try {
                return !isTokenExpired(token);
            } catch (ExpiredJwtException e) {
                log.warn("토큰이 만료되었습니다.");
                return false; // 만료된 토큰은 유효하지 않음
            } catch (JwtException e) {
                log.warn("유효하지 않은 JWT 토큰입니다.");
                return false; // 유효하지 않은 토큰 처리
            }
        }


    // 리프레시 토큰에서 사용자 ID를 추출하는 메서드
        public String getUserIdFromRefreshToken (String token){
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        }
    }