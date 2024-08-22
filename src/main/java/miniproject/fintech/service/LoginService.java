package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.service.memberservice.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final EntityConverter entityConverter;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final DtoConverter dtoConverter;

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean registeredMember(BankMemberDto bankMemberDto) {
        validateMemberDto(bankMemberDto);

        if (isDuplicateMember(bankMemberDto.getId())) {
            return false;
        }

        bankMemberDto.setPassword(encodePassword(bankMemberDto.getPassword()));
        BankMember bankMember = entityConverter.convertToBankMember(bankMemberDto);
        memberRepository.save(bankMember);
        return true;
    }

    private void validateMemberDto(BankMemberDto bankMember) {
        if (bankMember.getId() == null || bankMember.getPassword() == null) {
            throw new IllegalArgumentException("ID와 비밀번호는 null일 수 없습니다.");
        }
    }

    private boolean isDuplicateMember(Long memberId) {
        Optional<BankMember> findMember = memberRepository.findById(memberId);
        return findMember.isPresent();
    }

    public Optional<BankMemberDto> loginCheck(BankMemberDto bankMemberDto) {
        Optional<BankMember> findMember = memberRepository.findById(bankMemberDto.getId());
        if (findMember.isPresent()) {
            BankMember bankMember = findMember.get();
            if (passwordEncoder.matches(bankMemberDto.getPassword(), bankMember.getPassword())) {
                BankMemberDto rstBankMember = dtoConverter.convertToBankMemberDto(bankMember);
                rstBankMember.setId(bankMember.getId());
                rstBankMember.setPassword(bankMember.getPassword());

                return Optional.of(rstBankMember);
            }
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