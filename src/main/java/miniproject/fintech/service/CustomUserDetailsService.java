package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Autowired
    public CustomUserDetailsService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // admin 사용자 하드코딩 처리
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password(passwordEncoder.encode("adminpassword")) // 실제 어드민 비밀번호로 대체
                    .authorities("ROLE_ADMIN")
                    .build();
        }

        // 일반 사용자 (데이터베이스에서 조회)
        try {
            Long userId = Long.parseLong(username); // JWT 토큰에서 사용자 ID가 문자열로 제공된다고 가정

            BankMember member = memberRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

            return User.withUsername(String.valueOf(member.getId())) // 사용자 ID를 사용하여 UserDetails 생성
                    .password(member.getPassword()) // 암호는 이미 인코딩된 상태로 가정
                    .authorities("ROLE_USER") // 데이터베이스에 저장된 역할을 사용하도록 수정 가능
                    .build();
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + username);
        }
    }
}