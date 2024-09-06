package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final DtoConverter dtoConverter;
    private final EntityConverter entityConverter;

    public Optional<BankMember> findByUserId(String userId) {
        log.info("Searching for BankMember with userId: {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            log.error("Invalid userId: {}", userId);
            throw new CustomError(MEMBER_NOT_FOUND);  // userId가 null이거나 빈 문자열인 경우 예외 발생
        }

        Optional<BankMember> member = memberRepository.findByUserId(userId);
        if (member.isEmpty()) {
            log.warn("BankMember not found with userId: {}", userId);
            throw new CustomError(MEMBER_NOT_FOUND);  // 사용자를 찾을 수 없는 경우 예외 발생
        }

        return member;
    }



    public List<BankMemberDto> findAll() {
        log.info("모든 은행 회원 찾기");
        List<BankMember> members = memberRepository.findAll();
        return members.stream()
                .map(dtoConverter::convertToBankMemberDto)
                .collect(Collectors.toList());
    }

    public void userChangePassword(String userId, String oldPassword, String newPassword) {
        log.info("사용자 비밀번호 변경 시도: 사용자 ID {}", userId);
        BankMember member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            log.error("현재 비밀번호가 일치하지 않습니다: 사용자 ID {}", userId);
            throw new CustomError(PASSWORD_INCORRECT);
        }

        changePassword(userId, newPassword);
        log.info("비밀번호 변경 성공: 사용자 ID - {}", userId);
    }

    @Transactional
    public BankMemberDto createBankMember(BankMemberDto bankMemberDto, String roles) {
        log.info("새 은행 회원 생성 요청: {}", bankMemberDto);
        if (bankMemberDto.getPassword() == null || bankMemberDto.getPassword().isEmpty()) {
            throw new CustomError(PASSWORD_MUST_NOT_NULL);
        }

        validationCreateNewMember(bankMemberDto);

        String encodedPassword = passwordEncoder.encode(bankMemberDto.getPassword());
        String memberRoles = roles != null && !roles.isEmpty() ? roles : "USER";

        BankMember newBankMember = BankMember.builder()
                .userId(bankMemberDto.getUserId())
                .name(bankMemberDto.getName())
                .email(bankMemberDto.getEmail())
                .isActive(bankMemberDto.isActive())
                .amount(bankMemberDto.getAmount())
                .age(bankMemberDto.getAge())
                .birth(bankMemberDto.getBirth())
                .password(encodedPassword)
                .address(bankMemberDto.getAddress())
                .createdAt(bankMemberDto.getCreatedAt())
                .roles(memberRoles)
                .build();

        BankMember savedMember = memberRepository.save(newBankMember);
        log.info("새 은행 회원 생성 성공: {}", savedMember);
        return dtoConverter.convertToBankMemberDto(savedMember);
    }

    public BankMember createOrUpdateBankMember(BankMemberDto bankMemberDto) {
        // 유효성 검사
        if (bankMemberDto.getUserId() == null || bankMemberDto.getUserId().trim().isEmpty()) {
            throw new CustomError(MEMBER_NOT_FOUND);
        }

        // userId로 기존 회원을 조회
        Optional<BankMember> existingMemberOpt = memberRepository.findByUserId(bankMemberDto.getUserId());

        BankMember bankMember;
        if (existingMemberOpt.isPresent()) {
            // 기존 회원이 존재하면 해당 회원 정보를 업데이트
            bankMember = existingMemberOpt.get();
            entityConverter.updateBankMemberFromDto(bankMemberDto, bankMember);
        } else {
            // 기존 회원이 없으면 새로 생성
            bankMember = entityConverter.convertToBankMember(bankMemberDto);
        }

        // 데이터베이스에 엔티티 저장
        return memberRepository.save(bankMember);
    }



    @Transactional
    public BankMemberDto updateMember(String userId, BankMemberDto updatedMemberDto) {
        log.info("은행 회원 업데이트 요청: userId - {}, 내용 - {}", userId, updatedMemberDto);

        BankMember existingMember = validationOfId(userId);

        // 엔티티 변환기 사용하여 DTO에서 엔티티로 업데이트
        entityConverter.updateBankMemberFromDto(updatedMemberDto, existingMember);

        BankMember savedMember = memberRepository.save(existingMember);
        log.info("은행 회원 업데이트 성공: {}", savedMember);

        return dtoConverter.convertToBankMemberDto(savedMember);
    }

    @Transactional
    public void deleteById(String userId, String password) {
        log.info("은행 회원 삭제 시도: userId - {}", userId);
        BankMember bankMember = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        deleteValidation(userId, password, bankMember);
    }

    private BankMember validationOfId(String userId) {
        if (userId == null) {
            log.error("userId가 null입니다.");
            throw new CustomError(ID_NULL);
        }

        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AccountDto> findAccountByMemberId(String userId) {
        log.info("회원 userId로 계좌 찾기: userId - {}", userId);
        BankMember member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        return member.getAccounts().stream()
                .map(dtoConverter::convertToAccountDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BankMemberDto getBankMemberById(String userId) {
        log.info("회원 userId로 은행 회원 찾기: userId - {}", userId);
        BankMember member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        return dtoConverter.convertToBankMemberDto(member);
    }

    public Page<BankMemberDto> findAll(Pageable pageable) {
        log.info("페이지를 사용하여 모든 은행 회원 찾기: {}", pageable);
        Page<BankMember> members = memberRepository.findAll(pageable);
        return members.map(dtoConverter::convertToBankMemberDto);
    }

    public void changePassword(String userId, String password) {
        BankMember member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        String newPassword = passwordEncoder.encode(password);
        member.setPassword(newPassword);

        memberRepository.save(member); // 변경된 비밀번호를 저장합니다.
    }

    public boolean existsById(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    private void validationCreateNewMember(BankMemberDto bankMember) {
        Optional<BankMember> byEmail = memberRepository.findByEmail(bankMember.getEmail());
        Optional<BankMember> byUserId = memberRepository.findByUserId(bankMember.getUserId());

        log.info("새 은행 회원 검증 중: 이메일 - {}, 사용자 ID - {}", bankMember.getEmail(), bankMember.getUserId());

        if (byEmail.isPresent()) {
            log.error("이미 사용 중인 이메일: {}", bankMember.getEmail());
            throw new CustomError(EMAIL_DUPLICATE);
        }

        if (byUserId.isPresent()) {
            log.error("이미 사용 중인 사용자 ID: {}", bankMember.getUserId());
            throw new CustomError(MEMBER_EXIST);
        }
    }

    private void deleteValidation(String userId, String password, BankMember bankMember) {
        if (bankMember.getPassword() == null) {
            log.error("비밀번호가 null입니다: 은행 회원 userId - {}", userId);
            throw new CustomError(PASSWORD_MUST_NOT_NULL);
        }

        if (!passwordEncoder.matches(password, bankMember.getPassword())) {
            log.error("비밀번호 불일치: 은행 회원 userId - {}", userId);
            throw new CustomError(PASSWORD_INCORRECT);
        }

        log.info("비밀번호 일치. 은행 회원 삭제: userId - {}", userId);
        memberRepository.deleteByUserId(userId);
    }
}