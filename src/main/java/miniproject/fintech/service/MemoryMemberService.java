package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryMemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder  passwordEncoder;
    private final DtoConverter converter;

    // ID로 조회하여 DTO로 반환
    public Optional<BankMemberDto> findById(Long id) {
        log.info("ID로 은행 회원 찾기: {}", id);

        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        log.info("은행 회원 찾기 성공: {}", bankMember);
        return Optional.ofNullable(converter.convertToBankMemberDto(bankMember));
    }

    // 모든 회원 목록을 DTO 목록으로 반환
    public List<BankMemberDto> findAll() {
        log.info("모든 은행 회원 찾기");
        List<BankMember> members = memberRepository.findAll();
        return members.stream()
                .map(converter::convertToBankMemberDto)
                .collect(Collectors.toList());
    }

    // 비밀번호 변경
    public void userChangePassword(Long bankMemberId, String oldPassword, String newPassword) {
        log.info("사용자 비밀번호 변경 시도 : 사용자 ID {}", bankMemberId);
        BankMember member = memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            log.error("현재 비밀번호가 일치하지 않습니다 : 사용자 ID {}", bankMemberId);
            throw new CustomError(PASSWORD_INCORRECT);
        }

        changePassword(bankMemberId, newPassword);
        log.info("비밀번호 변경 성공: 사용자 ID - {}", bankMemberId);
    }

    // 회원 생성 메서드
    @Transactional
    public BankMemberDto createBankMember(BankMemberDto bankMemberDto, Set<String> roles) {
        log.info("새 은행 회원 생성 요청: {}", bankMemberDto);
        // 비밀번호 유효성 검사
        if (bankMemberDto.getPassword() == null || bankMemberDto.getPassword().isEmpty()) {
            throw new CustomError(PASSWORD_MUST_NOT_NULL);
        }

        validationCreateNewMember(bankMemberDto);

        String encodedPassword = passwordEncoder.encode(bankMemberDto.getPassword());

        // 기본 역할 'USER'를 설정
        String memberRoles = "USER";

        // 추가 역할이 있으면 쉼표로 구분하여 설정
        if (roles != null && !roles.isEmpty()) {
            memberRoles = String.join(",", roles); // Set을 쉼표로 구분된 문자열로 변환
        }

        BankMember newBankMember = BankMember.builder()
                .name(bankMemberDto.getName())
                .email(bankMemberDto.getEmail())
                .isActive(true)
                .password(encodedPassword)
                .address(bankMemberDto.getAddress())
                .createdAt(bankMemberDto.getCreatedAt())
                .accountNumber(bankMemberDto.getAccountNumber())
                .roles(memberRoles)
                .build();

        BankMember savedMember = memberRepository.save(newBankMember);
        log.info("새 은행 회원 생성 성공: {}", savedMember);
        return converter.convertToBankMemberDto(savedMember);
    }

    @Transactional
    public BankMemberDto updateMember(Long id, BankMemberDto updatedMemberDto) {
        log.info("은행 회원 업데이트 요청: ID - {}, 내용 - {}", id, updatedMemberDto);

        BankMember existingMember = validationOfId(id);  // 기존 회원 조회

        // 비밀번호 변경 로직 추가
        if (updatedMemberDto.getPassword() != null && !updatedMemberDto.getPassword().isEmpty()) {
            String newPassword = passwordEncoder.encode(updatedMemberDto.getPassword());
            existingMember.setPassword(newPassword);
        }

        // 다른 정보 업데이트
        existingMember.setName(updatedMemberDto.getName());
        existingMember.setAddress(updatedMemberDto.getAddress());
        existingMember.setEmail(updatedMemberDto.getEmail());

        BankMember savedMember = memberRepository.save(existingMember);
        log.info("은행 회원 업데이트 성공: {}", savedMember);

        return converter.convertToBankMemberDto(savedMember);
    }


    // 회원 삭제 메서드
    @Transactional
    public void deleteById(Long id, String password) {
        log.info("은행 회원 삭제 시도: ID - {}", id);
        BankMember bankMember = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        deleteValidation(id, password, bankMember);
    }

    private BankMember validationOfId(Long bankMemberId) {
        if (bankMemberId == null) {
            log.error("ID가 null입니다.");
            throw new CustomError(ID_NULL);
        }

        return memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AccountDto> findAccountByMemberId(Long id) {
        log.info("회원 ID로 계좌 찾기: ID - {}", id);
        BankMember member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        return member.getAccounts().stream()
                .map(converter::convertToAccountDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BankMemberDto getBankMemberById(Long bankMemberId) {
        log.info("회원 ID로 은행 회원 찾기: ID - {}", bankMemberId);
        BankMember member = memberRepository.findById(bankMemberId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        return converter.convertToBankMemberDto(member);
    }

    public Page<BankMemberDto> findAll(Pageable pageable) {
        log.info("페이지를 사용하여 모든 은행 회원 찾기: {}", pageable);
        Page<BankMember> page = memberRepository.findAll(pageable);
        return page.map(converter::convertToBankMemberDto);
    }

    public void changePassword(Long id, String password) {
        BankMember member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        String newPassword = passwordEncoder.encode(password);
        member.setPassword(newPassword);

        memberRepository.save(member); // 변경된 비밀번호를 저장합니다.
    }

    public boolean existsById(Long id) {
        return memberRepository.existsById(id);
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

    private void deleteValidation(Long id, String password, BankMember bankMember) {
        if (bankMember.getPassword().equals(password)) {
            log.info("비밀번호 일치. 은행 회원 삭제: ID - {}", id);
            memberRepository.deleteById(id);
        } else {
            log.error("비밀번호 불일치: 은행 회원 ID - {}", id);
            throw new CustomError(PASSWORD_INCORRECT);
        }
    }
}