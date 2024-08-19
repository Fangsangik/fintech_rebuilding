package miniproject.fintech.service.accountservice;

import miniproject.fintech.dto.AccountDto;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    AccountDto save(AccountDto account);

    Optional<AccountDto> findById(Long id);

    List<AccountDto> findAll();

    AccountDto createAccountForMember(AccountDto accountDto, Long memberId);

    void delete(Long accountId);

    AccountDto updateAccount(Long accountId, AccountDto updatedAccount);

    long getAccountBalance(Long id);

    long getTotalAccountBalance();

    boolean existsById(Long id);
}