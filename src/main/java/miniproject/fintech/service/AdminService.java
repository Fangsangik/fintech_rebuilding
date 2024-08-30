package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final DtoConverter converter;
    private final TransactionRepository transactionRepository;

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
    public BankMemberDto toggleUserActivation(Long id, boolean isActive) {
        BankMember bankMember = findBankMember(id);

        bankMember.setActive(isActive);
        BankMember savedBankMember = memberRepository.save(bankMember);
        log.info("사용자 활성화/비활성화 완료: 사용자 ID = {}, 활성 상태 = {}", id, isActive);
        return converter.convertToBankMemberDto(savedBankMember);
    }

    @Transactional
    public BankMemberDto updateUserDetails(Long id, BankMemberDto bankMemberDto) {
        BankMember bankMember = findBankMember(id);

        // 올바른 데이터 매핑
        bankMember.setName(bankMemberDto.getName());
        bankMember.setEmail(bankMemberDto.getEmail());
        bankMember.setAge(bankMemberDto.getAge());
        bankMember.setAddress(bankMemberDto.getAddress());

        BankMember updatedBankMember = memberRepository.save(bankMember);
        log.info("사용자 정보 수정 완료: 사용자 ID = {}", updatedBankMember.getId());
        return converter.convertToBankMemberDto(updatedBankMember);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions(int pageNum, int pageSize) {
        log.info("모든 거래 조회 시작");
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

        List<TransactionDto> transactionDtos = transactionPage
                .stream().map(converter::convertToTransactionDto)
                .collect(Collectors.toList());

        log.info("모든 거래 조회 완료: 총 거래 수 = {}", transactionPage.getTotalElements());

        return transactionDtos;
    }

    private BankMember findBankMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }
}