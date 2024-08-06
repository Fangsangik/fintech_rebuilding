package miniproject.fintech.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.repository.JpaMemberRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.MemberRepositoryImpl;
import miniproject.fintech.service.MemberService;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
