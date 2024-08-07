package miniproject.fintech.repository.accountrepository;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor //생성자 주입이 자동으로 진행되기 때문에 @Autowired 생략 가능
public class AccountRepositoryImpl implements AccountRepository{

    //@Autowired
    private final JpaAccountRepository accountRepository;

    //@Autowired
    private final JpaMemberRepository memberRepository;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account createdByBankMemberAccount(BankMember bankMember, Account account) {
        Optional<BankMember> member = memberRepository.findById(bankMember.getId());

        if (member.isPresent()) {
            BankMember existingMember = member.get();

            Optional<Account> existingAccount = accountRepository.findByBankMemberId(existingMember);

            if (existingAccount.isPresent()) {
                return existingAccount.get();
            } else {
                account.setBankMember(existingMember);
                accountRepository.save(account);
                return account;
            }
        }

        // BankMember가 존재하지 않으면 null
        return null;
    }

    @Override
    public void deletedByBankMember(BankMember bankMember, Long accountNumber) {
       Optional<BankMember> member = memberRepository.findById(bankMember.getId());

       if (member.isPresent()) {
           BankMember existingMember = member.get();

           if (isExisit(existingMember, accountNumber)) {
               Optional<Account> accountToDelete = existingMember.getAccounts()
                       .stream().filter(account -> account.getAccountNumber() == accountNumber)
                       .findFirst();

               accountToDelete.ifPresent(account -> {
                   existingMember.getAccounts().remove(account);
                   accountRepository.delete(account);
               });
           }
       }
    }

    private static boolean isExisit(BankMember bankMember, long accountNumber) {
        for (Account account : bankMember.getAccounts()) {
            if (account.getAccountNumber() == accountNumber) {
                return true;
            }
        }

        return false;
    }
}
