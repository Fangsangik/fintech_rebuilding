package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Admin;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.AdminRepository;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    //사용자 정보 조회
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if (userId == null || userId.trim().isEmpty()) {
            log.error("Invalid user ID: {}", userId);
            throw new UsernameNotFoundException("유효하지 않은 사용자 ID입니다: " + userId);
        }

        // 회원 데이터베이스에서 사용자 검색
        Optional<BankMember> optionalMember = memberRepository.findByUserId(userId);
        if (optionalMember.isPresent()) {
            BankMember member = optionalMember.get();
            log.info("User found with ID: {}", userId);

            return User.withUsername(member.getUserId())
                    .password(member.getPassword())
                    .authorities("ROLE_USER")
                    .build();
        }

        // 어드민 데이터베이스에서도 사용자 검색
        Optional<Admin> optionalAdmin = adminRepository.findByAdminId(userId);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            log.info("Admin found with ID: {}", userId);

            return User.withUsername(admin.getAdminId())
                    .password(admin.getPassword())
                    .authorities("ROLE_ADMIN")
                    .build();
        }

        log.error("User not found with ID: {}", userId);
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId);
    }
}