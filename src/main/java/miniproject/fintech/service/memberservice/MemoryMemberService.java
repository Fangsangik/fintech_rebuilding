package miniproject.fintech.service.memberservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryMemberService implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public BankMember save(BankMember bankMember) {
        log.info("은행 회원 저장: {}", bankMember);
        return memberRepository.save(bankMember);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankMember> findById(Long id) {
        if (id == null) {
            log.error("findById 메서드에서 ID가 null입니다.");
            throw new CustomError(ID_NULL);
        }
        log.info("ID로 은행 회원 찾기: {}", id);
        return memberRepository.findById(id);
    }

    @Override
    public List<BankMember> findAll() {
        log.info("모든 은행 회원 찾기");
        return new ArrayList<>(memberRepository.findAll());
    }

    @Override
    @Transactional
    public BankMember createBankMember(BankMemberDto bankMemberDto) {
        log.info("새 은행 회원 생성 요청: {}", bankMemberDto);
        validationCreateNewMember(bankMemberDto);

        BankMember newBankMember = BankMember.builder()
                .name(bankMemberDto.getName())
                .email(bankMemberDto.getEmail())
                .password(bankMemberDto.getPassword())
                .address(bankMemberDto.getAddress())
                .createdAt(bankMemberDto.getCreatedAt())
                .accountNumber(bankMemberDto.getAccountNumber())
                .build();

        BankMember savedMember = memberRepository.save(newBankMember);
        log.info("새 은행 회원 생성 성공: {}", savedMember);
        return savedMember;
    }

    private void validationCreateNewMember(BankMemberDto bankMember) {
        log.info("새 은행 회원 검증 중: 이메일 - {}, 계좌번호 - {}",
                bankMember.getEmail(), bankMember.getAccountNumber());

        Optional<BankMember> existingMemberByEmail = memberRepository.findByEmail(bankMember.getEmail());
        if (existingMemberByEmail.isPresent()) {
            log.error("이미 사용 중인 이메일: {}", bankMember.getEmail());
            throw new CustomError(EMAIL_DUPLICATE);
        }

        Optional<BankMember> existingMemberByAccountNumber = memberRepository.findByAccountNumber(bankMember.getAccountNumber());
        if (existingMemberByAccountNumber.isPresent()) {
            log.error("이미 사용 중인 계좌번호: {}", bankMember.getAccountNumber());
            throw new CustomError(ACCOUNT_NUMBER_DUPLICATE);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id, String password) {
        log.info("은행 회원 삭제 시도: ID - {}", id);
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        deleteValidation(id, password, bankMember);
    }

    private void deleteValidation(Long id, String password, BankMember bankMember) {
        if (bankMember.getPassword().equals(password)) {
            log.info("비밀번호 일치. 은행 회원 삭제: ID - {}", id);
            memberRepository.deleteById(id);
        } else {
            log.error("비밀번호 불일치: 은행 회원 ID - {}", id);
            throw new CustomError(PASSWORD_INCORRECT);
        }
    }

    @Override
    @Transactional
    public BankMember updateMember(BankMember bankMember, BankMemberDto updatedMemberDto) {
        log.info("은행 회원 업데이트 요청: ID - {}, 내용 - {}", bankMember.getId(), updatedMemberDto);
        BankMember existingMember = validationOfId(bankMember.getId());

        BankMember updatedBankMember = existingMember.toBuilder()
                .age(updatedMemberDto.getAge())
                .name(updatedMemberDto.getName())
                .address(updatedMemberDto.getAddress())
                .email(updatedMemberDto.getEmail())
                .build();

        BankMember savedMember = memberRepository.save(updatedBankMember);
        log.info("은행 회원 업데이트 성공: {}", savedMember);
        return savedMember;
    }

    private BankMember validationOfId(Long bankMemberId) {
        if (bankMemberId == null) {
            log.error("ID가 null입니다.");
            throw new CustomError(ID_NULL);
        }

        return memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }

    @Override
    public List<Account> findAccountByMemberId(Long id) {
        log.info("회원 ID로 계좌 찾기: ID - {}", id);
        BankMember member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        return new ArrayList<>(member.getAccounts());
    }

    @Override
    public BankMember getBankMemberById(Long bankMemberId) {
        log.info("회원 ID로 은행 회원 찾기: ID - {}", bankMemberId);
        return memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }

    @Override
    public Page<BankMember> findAll(Pageable pageable) {
        log.info("페이지를 사용하여 모든 은행 회원 찾기: {}", pageable);
        return memberRepository.findAll(pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
    }
}