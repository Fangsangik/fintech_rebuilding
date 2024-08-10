package miniproject.fintech.service.memberservice;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MemberServiceTest {

    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
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
    void findById_ValidId_ShouldReturnBankMember() {
        BankMember bankMember = BankMember.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("securepassword")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .accountNumber("1234567890")
                .build();

        BankMember savedMember = memberRepository.save(bankMember);

        Optional<BankMember> foundMember = memberService.findById(savedMember.getId());

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get()).usingRecursiveComparison().isEqualTo(savedMember);
    }

    @Test
    void findById_InvalidId_ShouldReturnEmpty() {
        BankMember nonExistentMember = BankMember.builder()
                .id(999L)
                .build();

        Optional<BankMember> foundMember = memberService.findById(nonExistentMember.getId());

        assertThat(foundMember).isEmpty();
    }

    @Test
    void findById_InvalidBankMember_ShouldThrowException() {
        //given
        BankMember invalidMember = BankMember.builder()
                .id(null)
                .build();

        // CustomError가 발생해야 함
        assertThrows(CustomError.class, () -> memberService.findById(invalidMember.getId()));
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
    void createBankMember() {
        // Given: BankMemberDto 객체를 생성
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .name("John Doe")
                .password("123")
                .email("john.doe@example.com")
                .password("securepassword")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .accountNumber("1234567890")
                .build();

        // When: createBankMember 메서드를 호출하여 새 BankMember를 생성
        BankMember createdMember = memberService.createBankMember(bankMemberDto);

        // Then: 생성된 BankMember가 null이 아니고, BankMemberDto의 필드와 일치해야 함
        assertNotNull(createdMember, "The created BankMember should not be null");
        assertEquals(bankMemberDto.getName(), createdMember.getName(), "The names should match");
        assertEquals(bankMemberDto.getEmail(), createdMember.getEmail(), "The emails should match");
        assertEquals(bankMemberDto.getAccountNumber(), createdMember.getAccountNumber(), "The account numbers should match");
    }

    @Test
    @Transactional
    void delete() {
        // Given: 기존 회원을 데이터베이스에 저장
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("securepassword")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .accountNumber("1234567890")
                .build();

        BankMember rst = memberService.createBankMember(bankMemberDto);
        Long memberId = rst.getId();

        // 비밀번호가 틀렸을 때 CustomError가 발생해야 함
        Exception exception = assertThrows(CustomError.class,
                () -> {
                    memberService.deleteById(memberId, "wrongpassword");
                });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}