package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.AdminRepository;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository, AdminRepository adminRepository) {
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 ID가 숫자 형식인지 확인
        if (!username.matches("\\d+")) {
            log.error("Invalid user ID format: " + username);
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + username);
        }

        try {
            Long userId = Long.parseLong(username); // JWT 토큰에서 사용자 ID가 문자열로 제공된다고 가정

            BankMember member = memberRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: " + userId);
                        return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
                    });

            log.info("User found with ID: " + userId);

            return User.withUsername(String.valueOf(member.getId())) // 사용자 ID를 사용하여 UserDetails 생성
                    .password(member.getPassword()) // 암호는 이미 인코딩된 상태로 가정
                    .authorities("USER") // 데이터베이스에 저장된 역할을 사용하도록 수정 가능
                    .build();
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: " + username, e);
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: " + username);
        }
    }
}