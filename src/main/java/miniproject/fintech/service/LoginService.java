package miniproject.fintech.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoConverter dtoConverter;


    public Optional<BankMemberDto> loginCheck(BankMemberDto bankMemberDto) {
        Optional<BankMember> findMember = memberRepository.findById(bankMemberDto.getId());
        if (findMember.isPresent() && passwordEncoder.matches(bankMemberDto.getPassword(), findMember.get().getPassword())) {
            // 비밀번호가 일치하면 성공
            BankMemberDto dto = dtoConverter.convertToBankMemberDto(findMember.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long bankMemberId = (Long) session.getAttribute("bankMember");
            log.info("로그아웃 성공, memberId : {}", bankMemberId);
            session.invalidate();
        } else {
            log.warn("로그아웃 시도 중 세션이 존재하지 않음.");
        }
    }
}