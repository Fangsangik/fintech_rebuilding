package miniproject.fintech.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.config.JwtTokenUtil;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;


    // 블랙리스트를 메모리 기반으로 구현 (실제 사용 시 Redis 등 외부 저장소 사용 권장)
    private final Set<String> tokenBlacklist = new HashSet<>();

    public Optional<String> loginCheck(BankMemberDto bankMemberDto) {
        Optional<BankMember> findMember = memberRepository.findByUserId(bankMemberDto.getUserId());
        if (findMember.isPresent() && passwordEncoder.matches(bankMemberDto.getPassword(), findMember.get().getPassword())) {
            // 비밀번호가 일치하면 성공
            String jwtToken = jwtTokenUtil.generateToken(bankMemberDto.getUserId());
            log.info("로그인 성공: ID={}", bankMemberDto.getUserId());
            return Optional.of(jwtToken);
        }

        log.warn("로그인 실패: 사용자 ID {}의 인증에 실패했습니다.", bankMemberDto.getUserId());
        return Optional.empty();
    }

    public void logout(HttpServletRequest request) {
        String jwtToken = extractJwtFromRequest(request);
        if (jwtToken != null) {
            tokenBlacklist.add(jwtToken);  // 토큰을 블랙리스트에 추가
            log.info("로그아웃 성공: 토큰 블랙리스트에 추가됨.");
        } else {
            log.warn("로그아웃 요청 시 토큰이 제공되지 않음.");
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isTokenBlackListed(String token) {
        return tokenBlacklist.contains(token);
    }

    public String generateRefreshToken(String userId) {
        return jwtTokenUtil.generateToken(userId);
    }

    public Optional<String> refreshToken(String refreshToken) {
        if (jwtTokenUtil.validateToken(refreshToken)) {
            String userIdFromRefreshToken = jwtTokenUtil.getUserIdFromRefreshToken(refreshToken);
            return Optional.of(generateRefreshToken(userIdFromRefreshToken));
        }

        return Optional.empty();
    }
}