package miniproject.fintech.service.accountservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.service.memberservice.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static miniproject.fintech.type.ErrorType.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberService memberService;
    private final DtoConverter dtoConverter;
    private final EntityConverter entityConverter;

    @Override
    public AccountDto save(AccountDto accountDto) {
        log.info("계좌 저장 요청: {}", accountDto);
        Account account = entityConverter.convertToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        log.info("계좌 저장 성공: {}", savedAccount);
        return dtoConverter.convertToAccountDto(savedAccount);
    }


    @Override
    public Optional<AccountDto> findById(Long id) {
        log.info("계좌 조회 요청: ID = {}", id);
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            log.info("계좌 조회 성공: {}", account.get());
            return Optional.of(dtoConverter.convertToAccountDto(account.get()));
        } else {
            log.warn("계좌 조회 실패: ID = {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<AccountDto> findAll() {
        log.info("모든 계좌 조회 요청");
        List<Account> accounts = accountRepository.findAll();
        log.info("모든 계좌 조회 성공: 총 {}개 계좌", accounts.size());
        return dtoConverter.convertToAccountDtoList(accounts);
    }

    @Override
    @Transactional
    public AccountDto createAccountForMember(AccountDto accountDto, Long memberId) {
        log.info("회원 ID {}로 계좌 생성 요청: {}", memberId, accountDto);

        // BankMember를 찾습니다.
        BankMember bankMember = memberService.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: ID = {}", memberId);
                    return new CustomError(MEMBER_NOT_FOUND);
                });

        // 유효성 검사 및 계좌 생성
        validationCheckMember(bankMember, accountDto);

       Account account = entityConverter.convertToAccount(accountDto);
       account.setBankMember(bankMember);

        Account savedAccount = accountRepository.save(account);
        log.info("계좌 생성 성공: {}", savedAccount);
        return dtoConverter.convertToAccountDto(savedAccount);
    }

    private BankMember validationCheckMember(BankMember bankMember, AccountDto accountDto) {
        if (accountDto == null || bankMember == null) {
            log.error("회원 또는 계좌 DTO가 null입니다. 회원: {}, 계좌 DTO: {}", bankMember, accountDto);
            throw new CustomError(MUST_NOT_NULL);
        }

        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            log.error("계좌 번호 중복: {}", accountDto.getAccountNumber());
            throw new CustomError(ACCOUNT_NUMBER_DUPLICATE);
        }

        log.info("회원 및 계좌 DTO 유효성 검사 완료. 회원: {}, 계좌 DTO: {}", bankMember, accountDto);
        return bankMember;
    }

    @Override
    @Transactional
    public void delete(Long accountId) {
        log.info("계좌 삭제 요청: ID = {}", accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", accountId);
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        accountRepository.delete(account);

        if (accountRepository.existsById(accountId)) {
            log.error("계좌 삭제 실패: ID = {}", accountId);
            throw new CustomError(ACCOUNT_DELETE_FAILED);
        }

        log.info("계좌 삭제 성공: ID = {}", accountId);
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long accountId, AccountDto updatedAccountDto) {
        log.info("계좌 업데이트 요청: ID = {}, 업데이트 내용: {}", accountId, updatedAccountDto);
        Account existingAccount = validationOfId(accountId);

        Account updatedAccount = existingAccount.toBuilder()
                .accountNumber(updatedAccountDto.getAccountNumber())
                .amount(updatedAccountDto.getAmount())
                .accountStatus(updatedAccountDto.getAccountStatus())
                .build();

        Account savedAccount = accountRepository.save(updatedAccount);
        log.info("계좌 업데이트 성공: {}", savedAccount);
        return dtoConverter.convertToAccountDto(savedAccount);
    }

    private Account validationOfId(Long accountId) {
        if (accountId == null) {
            log.error("계좌 ID가 null입니다.");
            throw new CustomError(ID_NULL);
        }

        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", accountId);
                    return new CustomError(ACCOUNT_ID_NOT_FOUND);
                });
    }

    @Override
    public long getAccountBalance(Long id) {
        log.info("계좌 잔액 조회 요청: ID = {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("계좌 조회 실패: ID = {}", id);
                    return new CustomError(ACCOUNT_ID_NOT_FOUND);
                });

        long balance = account.getAmount();
        log.info("계좌 잔액 조회 성공: ID = {}, 잔액 = {}", id, balance);
        return balance;
    }

    @Override
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

    @Override
    public boolean existsById(Long id) {
        return accountRepository.existsById(id);
    }
}