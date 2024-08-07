package miniproject.fintech.config;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepositoryImpl;
import miniproject.fintech.service.memberservice.MemoryMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@RequiredArgsConstructor
public class MemberConfig {

    private final JpaMemberRepository jpaMemberRepository;
    private final MemberRepository memberRepository;

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryImpl(jpaMemberRepository);
    }

    @Bean
    public MemoryMemberService memberService() {
        return new MemoryMemberService(memberRepository);
    }
}
