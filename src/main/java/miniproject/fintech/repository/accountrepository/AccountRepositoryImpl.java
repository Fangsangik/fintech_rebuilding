package miniproject.fintech.repository.accountrepository;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor //생성자 주입이 자동으로 진행되기 때문에 @Autowired 생략 가능
public class AccountRepositoryImpl{

    //@Autowired
    private final AccountRepository accountRepository;

    //@Autowired
    private final MemberRepository memberRepository;

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

}
