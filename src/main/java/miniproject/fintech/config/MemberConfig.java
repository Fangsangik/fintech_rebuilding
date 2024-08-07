package miniproject.fintech.config;

import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import miniproject.fintech.repository.memberrepository.MemberRepositoryImpl;
import miniproject.fintech.service.memberservice.MemoryMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class MemberConfig {

    @Bean
    public MemberRepository memberRepository(JpaMemberRepository jpaMemberRepository) {
        return new MemberRepositoryImpl(jpaMemberRepository);
    }

    @Bean
    public MemoryMemberService memberService(MemberRepository memberRepository) {
        return new MemoryMemberService(memberRepository);
    }
}
