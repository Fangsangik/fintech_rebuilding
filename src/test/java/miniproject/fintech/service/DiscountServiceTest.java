package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class DiscountServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DiscountService discountService;

    @Test
    void discount() {
        // given
        BankMember bankMember = BankMember.builder()
                .userId("test" + UUID.randomUUID())
                .grade(Grade.VIP) // Grade.VIP로 설정
                .amount(2000)
                .password("password") // 비밀번호 설정
                .build();

        // 엔티티를 데이터베이스에 저장
        BankMember savedMember = memberRepository.save(bankMember);

        // when
        discountService.applyDiscount(savedMember.getUserId(), savedMember.getPassword());

        // then
        // 업데이트된 bankMember를 가져옵니다.
        BankMember updatedMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        double expectedDiscount = savedMember.getAmount() - savedMember.getAmount() * 0.01;
        // 소수점 비교를 위해 delta 값을 설정합니다.
        assertEquals(expectedDiscount, updatedMember.getAmount(), 0.01);
    }
}