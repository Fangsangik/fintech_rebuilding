package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static miniproject.fintech.type.ErrorType.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl {

    private final AccountRepository accountRepository;
    private final MemoryMemberService memberService;
    private final DtoConverter dtoConverter;
    private final EntityConverter entityConverter;

    @Transactional(readOnly = true)
    // ID로 계좌 조회 후 DTO 반환
    public Optional<AccountDto> findByAccountNumber(String accountNumber) {
        log.info("계좌 조회 요청: ID = {}", accountNumber);
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isPresent()) {
            log.info("계좌 조회 성공: {}", account.get());
            return Optional.of(dtoConverter.convertToAccountDto(account.get()));
        } else {
            log.warn("계좌 조회 실패: ID = {}", accountNumber);
            return Optional.empty();
        }
    }

    // 모든 계좌 조회 후 DTO 리스트 반환
    public List<AccountDto> findAll() {
        log.info("모든 계좌 조회 요청");
        List<Account> accounts = accountRepository.findAll();
        log.info("모든 계좌 조회 성공: 총 {}개 계좌", accounts.size());
        return dtoConverter.convertToAccountDtoList(accounts);
    }

    // 회원에게 계좌 생성 후 DTO 반환
    @Transactional
    public AccountDto createAccountForMember(AccountDto accountDto, Long memberId) {
        log.info("회원 ID {}로 계좌 생성 요청: {}", memberId, accountDto);

        // 회원 조회 및 검증
        BankMember bankMemberDto = memberService.findById(memberId);

        // 계좌 및 회원 검증
        validationCheckMember(dtoConverter.convertToBankMemberDto(bankMemberDto), accountDto);

        // DTO를 엔티티로 변환하여 계좌 생성
        Account account = entityConverter.convertToAccount(accountDto);
        account.setBankMember(bankMemberDto);

        Account savedAccount = accountRepository.save(account);
        log.info("계좌 생성 성공: {}", savedAccount);
        return dtoConverter.convertToAccountDto(savedAccount);
    }

    private void validationCheckMember(BankMemberDto bankMember, AccountDto accountDto) {
        if (accountDto == null || bankMember == null) {
            log.error("회원 또는 계좌 DTO가 null입니다. 회원: {}, 계좌 DTO: {}", bankMember, accountDto);
            throw new CustomError(MUST_NOT_NULL);
        }

        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            log.error("계좌 번호 중복: {}", accountDto.getAccountNumber());
            throw new CustomError(ACCOUNT_NUMBER_DUPLICATE);
        }

        log.info("회원 및 계좌 DTO 유효성 검사 완료. 회원: {}, 계좌 DTO: {}", bankMember, accountDto);
    }

    // 계좌 삭제
    @Transactional
    public void delete(String accountNumber) {
        log.info("계좌 삭제 요청: ID = {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", accountNumber);
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        accountRepository.delete(account);

        if (accountRepository.existsByAccountNumber(accountNumber)) {
            log.error("계좌 삭제 실패: ID = {}", accountNumber);
            throw new CustomError(ACCOUNT_DELETE_FAILED);
        }

        log.info("계좌 삭제 성공: ID = {}", accountId);
    }

    // 계좌 업데이트 후 DTO 반환
    @Transactional
    public AccountDto updateAccount(String accountNumber, AccountDto updatedAccountDto) {
        log.info("계좌 업데이트 요청: ID = {}, 업데이트 내용: {}", accountNumber, updatedAccountDto);
        Account existingAccount = validationOfAccountNumber(accountNumber);

        // 업데이트된 정보를 사용하여 기존 계좌 업데이트
        existingAccount.setAccountNumber(updatedAccountDto.getAccountNumber());
        existingAccount.setAmount(updatedAccountDto.getAmount());
        existingAccount.setAccountStatus(updatedAccountDto.getAccountStatus());
        existingAccount.setAccountStatus(updatedAccountDto.getAccountStatus());
        existingAccount.setName(updatedAccountDto.getName());


        Account savedAccount = accountRepository.save(existingAccount);
        log.info("계좌 업데이트 성공: {}", savedAccount);
        return savedAccount;
    }

    private Account validationOfAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            log.error("계좌 ID가 null입니다.");
            throw new CustomError(ID_NULL);
        }

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", accountNumber);
                    return new CustomError(ACCOUNT_ID_NOT_FOUND);
                });
    }

    // 계좌 잔액 조회
    public long getAccountBalance(String accountNumber) {
        log.info("계좌 잔액 조회 요청: ID = {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", accountNumber);
                    return new CustomError(ACCOUNT_ID_NOT_FOUND);
                });

        long balance = account.getAmount();
        log.info("계좌 잔액 조회 성공: ID = {}, 잔액 = {}", accountNumber, balance);
        return balance;
    }

    // 모든 계좌의 총 잔액 조회
    @Transactional(readOnly = true)
    public long getTotalAccountBalance() {
        log.info("총 계좌 잔액 조회 요청");
        List<Account> allAccounts = accountRepository.findAll();
        long totalBalance = allAccounts.stream()
                .mapToLong(Account::getAmount)
                .sum();
        log.info("총 계좌 잔액 조회 성공: 총 잔액 = {}", totalBalance);
        return totalBalance;
    }

    // 계좌 존재 여부 확인
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
}
