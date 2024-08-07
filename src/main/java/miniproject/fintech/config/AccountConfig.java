package miniproject.fintech.config;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.repository.accountrepository.AccountRepository;
import miniproject.fintech.repository.accountrepository.AccountRepositoryImpl;
import miniproject.fintech.repository.accountrepository.JpaAccountRepository;
import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepositoryImpl;
import miniproject.fintech.service.accountservice.AccountService;
import miniproject.fintech.service.accountservice.AccountServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AccountConfig {

    private final JpaMemberRepository jpaMemberRepository;
    private final JpaAccountRepository jpaAccountRepository;

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryImpl(jpaMemberRepository);
    }

    @Bean
    public AccountRepository accountRepository() {
        return new AccountRepositoryImpl(jpaAccountRepository, jpaMemberRepository);
    }

    @Bean
    public AccountService accountService() {
        return new AccountServiceImpl(jpaMemberRepository, jpaAccountRepository);
    }
}