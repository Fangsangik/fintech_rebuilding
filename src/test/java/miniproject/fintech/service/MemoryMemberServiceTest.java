package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)

class MemoryMemberServiceTest {
    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DtoConverter converter;

    private BankMemberDto bankMemberDto;
    @Autowired
    private DtoConverter dtoConverter;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();

        BankMember bankMember = memberRepository.save(BankMember.builder()
                .name("Messi")
                .accountNumber("1234")
                .email("john.doe@example.com")
                .accounts(new ArrayList<>())
                .roles("USER")
                .address("seoul")
                .age(20)
                .build());

        bankMemberDto = converter.convertToBankMemberDto(bankMember);
    }

    @Transactional
    @Test
    void findById_ValidId_ShouldReturnBankMember() {


        BankMember foundMember = memberService.findById(bankMemberDto.getId());

        assertThat(foundMember).isNotNull();
        BankMember bankMember = foundMember;
        assertEquals(bankMemberDto.getId(), bankMember.getId());
    }

    @Transactional
    @Test
    void findById_InvalidId_ShouldReturnEmpty() {
        Long nonExistentMemberId = 999L;
        CustomError exception = assertThrows(CustomError.class, () -> {
            memberService.findById(nonExistentMemberId);
        });

        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Transactional
    @Test
    void findById_InvalidBankMember_ShouldThrowException() {
        //given
        Long invalidMemberId = null;
        CustomError exception = assertThrows(CustomError.class, () -> {
            memberService.findById(invalidMemberId);
        });

        // CustomError가 발생해야 함
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Transactional
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


        BankMember saved1 = memberRepository.save(bankMember1);

        // when
        List<BankMember> bankMembers = memberRepository.findAll();

        // then
        assertThat(bankMembers).hasSize(2);
    }

    @Test
    void createBankMember() {
        memberRepository.deleteAll();

        // Given: BankMemberDto 객체를 생성
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .name("John Doe")
                .password("123")
                .roles("USER")
                .email("apple.doe@example.com")
                .password("securepassword")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .accountNumber("1234567890")
                .build();

        // When: createBankMember 메서드를 호출하여 새 BankMember를 생성
        BankMember createdMember = memberService.createBankMember(bankMemberDto, bankMemberDto.getRoles());

        // Then: 생성된 BankMember가 null이 아니고, BankMemberDto의 필드와 일치해야 함
        assertNotNull(createdMember, "The created BankMember should not be null");
        assertEquals(bankMemberDto.getName(), createdMember.getName(), "The names should match");
        assertEquals(bankMemberDto.getEmail(), createdMember.getEmail(), "The emails should match");
        assertEquals(bankMemberDto.getAccountNumber(), createdMember.getAccountNumber(), "The account numbers should match");
    }

    @Test
    @Transactional
    void delete() {
        memberRepository.deleteAll();
        // Given: 기존 회원을 데이터베이스에 저장
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .name("John Doe")  // 필수 필드 추가
                .email("john.doe@example.com")  // 필수 필드 추가
                .password("1234")
                .roles("USER")  // 필수 필드 추가
                .address("123 Main St")  // 필수 필드 추가
                .createdAt(LocalDateTime.now())  // 필수 필드 추가
                .accountNumber("1234567890")  // 필수 필드 추가
                .build();

        BankMember rst = memberService.createBankMember(bankMemberDto, bankMemberDto.getRoles());
        Long memberId = rst.getId();

        // 비밀번호가 틀렸을 때 CustomError가 발생해야 함
        Exception exception = assertThrows(CustomError.class,
                () -> {
                    memberService.deleteById(memberId, "wrongpassword");
                });

        assertEquals("비밀번호가 null이면 안됩니다.", exception.getMessage());
    }
}