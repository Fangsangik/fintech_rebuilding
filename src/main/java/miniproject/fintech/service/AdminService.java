package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Admin;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AdminRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final DtoConverter converter;
    private final TransactionRepository transactionRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoConverter dtoConverter;
    private final EntityConverter entityConverter;

    @Transactional(readOnly = true)
    public List<BankMemberDto> getAllBankMembers(int pageNum, int pageSize) {
        log.info("모든 사용자 조회 시작");
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<BankMember> bankMemberPage = memberRepository.findAll(pageable);

        List<BankMemberDto> bankMemberDtos = bankMemberPage
                .stream().map(converter::convertToBankMemberDto)
                .collect(Collectors.toList());

        log.info("모든 사용자 조회 완료: 총 사용자 수 = {}", bankMemberPage.getTotalElements());

        return bankMemberDtos;
    }

    @Transactional
    public BankMemberDto toggleUserActivation(String userId, boolean isActive) {
        BankMember bankMember = findBankMember(userId);

        bankMember.setActive(isActive);
        BankMember savedBankMember = memberRepository.save(bankMember);
        log.info("사용자 활성화/비활성화 완료: 사용자 ID = {}, 활성 상태 = {}", userId, isActive);
        return dtoConverter.convertToBankMemberDto(savedBankMember);
    }

    @Transactional
    public BankMemberDto updateUserDetails(String userId, BankMemberDto bankMemberDto) {
        BankMember bankMember = findBankMember(userId);

        if (bankMemberDto.getName() != null) bankMember.setName(bankMemberDto.getName());
        if (bankMemberDto.getEmail() != null) bankMember.setEmail(bankMemberDto.getEmail());
        if (bankMemberDto.getAddress() != null) bankMember.setAddress(bankMemberDto.getAddress());

        BankMember updatedBankMember = memberRepository.save(bankMember);
        log.info("사용자 정보 수정 완료: 사용자 ID = {}", updatedBankMember.getId());
        return converter.convertToBankMemberDto(updatedBankMember);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions(int pageNum, int pageSize) {
        log.info("모든 거래 조회 시작");
        // 페이징 정보를 설정
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        // 페이지별 거래 조회
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        // 로깅: 총 거래 수를 출력
        log.info("모든 거래 조회 완료: 총 거래 수 = {}", transactionPage.getTotalElements());

        // 엔티티 리스트 반환
        return dtoConverter.convertToTransactionDtoList(transactionPage.getContent());  // Page 객체에서 거래 리스트를 직접 반환
    }

    @Transactional
    public AdminDto updateAdmin(String adminId, AdminDto adminDto) {
        log.info("관리자 정보 수정 요청: 관리자 ID - {}", adminId);

        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new CustomError(ErrorType.ADMIN_NOT_FOUND));

        validateAdmin(adminId);

        if (adminDto.getPassword() != null && !adminDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(adminDto.getPassword());
            admin.setPassword(encodedPassword);
        }

        if (adminDto.getName() != null) {
            admin.setName(adminDto.getName());
        }

        if (adminDto.getEmail() != null) {
            admin.setEmail(adminDto.getEmail());
        }

        Admin savedAdmin = adminRepository.save(admin);
        log.info("관리자 정보 수정 완료: 관리자 ID - {}", savedAdmin.getId());

        return dtoConverter.convertToAdminDto(savedAdmin);
    }

    public void adminChangeUserPassword(String adminId, String userId, String newPassword) {
        log.info("관리자에 의한 사용자 비밀번호 변경 요청: 관리자 ID - {}, 사용자 ID - {}", adminId, userId);

        validateAdmin(adminId);

        BankMember user = findBankMember(userId);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        memberRepository.save(user);
        log.info("비밀번호 변경 성공: 사용자 ID - {}", userId);
    }

    private BankMember findBankMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }

    private void validateAdmin(String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new CustomError(ErrorType.ADMIN_NOT_FOUND));

        if (!admin.getRoles().contains("ADMIN")) {
            log.error("접근 권한이 없습니다. 관리자 ID - {}", admin.getId());
            throw new CustomError(NOT_ALLOWED_ACCESS);
        }

        log.info("관리자 검증 성공: 관리자 ID - {}", adminId);
    }

    public void changeAdminPassword(String adminId, String newPassword) {
        log.info("관리자 비밀번호 변경 요청: 관리자 ID - {}", adminId);

        Admin admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new CustomError(ErrorType.ADMIN_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(encodedPassword);

        adminRepository.save(admin);
        log.info("비밀번호 변경 성공: 관리자 ID - {}", adminId);
    }
}
