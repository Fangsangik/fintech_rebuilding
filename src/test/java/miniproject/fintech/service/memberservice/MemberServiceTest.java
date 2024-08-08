package miniproject.fintech.service.memberservice;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//데이터베이스와 관련된 테스트에 최적화되어 있으며, 데이터베이스 관련 빈만 로드
class MemberServiceTest {

    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach has been setup");
    }

    @Test
    void saveMember() {
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

        BankMember saveMember = memberRepository.save(bankMember);
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

        BankMember saveMember2 = memberRepository.save(bankMember2);
        assertNotNull(saveMember2.getId());
        assertThat(saveMember2.getName()).isEqualTo("카카");
        assertThat(saveMember2.getPassword()).isEqualTo("987654321");
    }

    @Test
    void findById() {
        String uniqueAccountNumber = "acc" + System.currentTimeMillis(); //고유한 계좌 번호 생성
        BankMember bankMember = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber(uniqueAccountNumber)
                .id(1L)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        BankMember savedMember = memberRepository.save(bankMember);

        Optional<BankMember> findMember = memberRepository.findById(savedMember.getId());

        assertThat(findMember).isPresent();
        assertThat(findMember.get()).isEqualTo(savedMember);
    }

    @Test
    void findAll() {
        // given
        String uniqueAccountNumber1 = "acc" + System.currentTimeMillis(); // 고유한 계좌 번호 생성
        BankMember bankMember1 = BankMember.builder()
                .name("아리")
                .password("password1")
                .accountNumber(uniqueAccountNumber1)
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        String uniqueAccountNumber2 = "bcc" + System.currentTimeMillis(); // 또 다른 고유한 계좌 번호 생성
        BankMember bankMember2 = BankMember.builder()
                .name("카카")
                .password("password2")
                .accountNumber(uniqueAccountNumber2)
                .age(22)
                .birth(LocalDate.of(1995, 7, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        BankMember saved1 = memberRepository.save(bankMember1);
        BankMember saved2 = memberRepository.save(bankMember2);

        // when
        List<BankMember> bankMembers = memberRepository.findAll();

        // then
        assertThat(bankMembers).hasSize(2);
        assertThat(bankMembers).containsExactlyInAnyOrder(saved1, saved2);
    }

    @Test
    void create() {
        // Given: 기존 회원을 데이터베이스에 저장
        BankMember existBankMember = BankMember.builder()
                .name("아리")
                .password("123456789")
                .accountNumber("abc123")
                .age(20)
                .birth(LocalDate.of(1995, 6, 5))
                .createdAt(LocalDateTime.now())
                .grade(Grade.NORMAL)
                .address("서울")
                .build();

        memberRepository.save(existBankMember);

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
        memberRepository.save(bankMember1);

        // when
        memberService.delete(2L, "123456789");

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.findById(2L);
        }, "회원이 존재하지 않습니다.");
    }
}