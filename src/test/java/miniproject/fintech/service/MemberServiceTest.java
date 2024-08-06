package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach(){
        System.out.println("beforeEach has been setup");
    }

    @Test
    @Transactional
    void saveMember(){
        BankMember bankMember = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        BankMember saveMember = memberService.save(bankMember);
        assertNotNull(saveMember.getId());
        assertThat(saveMember.getName()).isEqualTo("아리");
        assertThat(saveMember.getPassword()).isEqualTo("123456789");

        BankMember bankMember2 = BankMember.builder()
                .name("카카")
                .password("987654321")
                .accountNumber("cba321")
                .age(22)
                .birth(LocalDate.of(1995, 7, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        BankMember saveMember2 = memberService.save(bankMember2);
        assertNotNull(saveMember2.getId());
        assertThat(saveMember2.getName()).isEqualTo("카카");
        assertThat(saveMember2.getPassword()).isEqualTo("987654321");
    }

    @Test
    void findById() {
        BankMember bankMember = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .id(2L)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        memberService.save(bankMember);

        BankMember findMember = memberService.findById(bankMember.getId());
        assertThat(findMember.getId()).isEqualTo(2L);
    }

    @Test
    @Transactional
    void findAll(){
        //given
        BankMember bankMember1 = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .id(2L)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        BankMember bankMember2 = BankMember.builder()
                .name("카카")
                .password("987654321")
                .accountNumber("cba321")
                .id(3L)
                .age(22)
                .birth(LocalDate.of(1995, 7, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        memberService.save(bankMember1);
        memberService.save(bankMember2);

        //when
        List<BankMember> bankMembers = memberService.findAll();

        //then
        assertThat(bankMembers).hasSize(2);
        assertThat(bankMembers).contains(bankMember1, bankMember2);
    }

    @Test
    @Transactional
    void create() {
        // Given: 기존 회원을 데이터베이스에 저장
        BankMember existBankMember = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .id(1L)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        memberService.save(existBankMember);

        // When: 중복된 ID로 새로운 회원 생성 시도
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.create(1L, "123456789");
        });

        // Then: 예외 메시지 검증
        assertThat(exception.getMessage()).isEqualTo("중복된 회원입니다.");

        // When: 중복되지 않는 ID로 새로운 회원 생성
        BankMember newBankMember = BankMember.builder()
                .name("끄로스")
                .password("newPassword")
                .accountNumber("a123456789")
                .id(2L)
                .age(20)
                .birth(LocalDate.of(1990, 8, 10))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();
        Optional<BankMember> createdBankUser = memberService.create(2L, "a123456789");

        // Then: 새로운 회원이 성공적으로 생성되었는지 검증
        assertThat(createdBankUser).isPresent();
        assertThat(createdBankUser.get().getId()).isEqualTo(2L);
        assertThat(createdBankUser.get().getAccountNumber()).isEqualTo("a123456789");

    }

    @Test
    void delete() {
        BankMember bankMember1 = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .id(2L)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();
        memberService.save(bankMember1);
        memberService.delete(2L, "123456789");

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.findById(2L);
        }, "회원이 존재하지 않습니다.");
    }

}