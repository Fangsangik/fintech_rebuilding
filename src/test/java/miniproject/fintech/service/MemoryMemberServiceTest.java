package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemoryMemberServiceTest {

    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DtoConverter converter;

    @Autowired
    private EntityConverter entityConverter;

    private BankMemberDto bankMemberDto;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();  // 중복을 방지하기 위해 모든 데이터를 삭제합니다.

        // 고유한 userId를 사용하여 BankMember 생성
        BankMember bankMember = memberRepository.save(BankMember.builder()
                .name("Messi")
                .userId("test_" + UUID.randomUUID())  // 고유한 userId 사용
                .email("john.doe@example.com")
                .accounts(new ArrayList<>())
                .roles("USER")
                .address("seoul")
                .age(20)
                .build());

        bankMemberDto = converter.convertToBankMemberDto(bankMember);
    }

    @Test
    void findById_ValidId_ShouldReturnBankMember() {
        Optional<BankMember> foundMember = memberService.findByUserId(bankMemberDto.getUserId());

        assertThat(foundMember).isPresent();
        BankMember bankMember = foundMember.get();
        assertEquals(bankMemberDto.getId(), bankMember.getId());
    }

    @Test
    void findById_InvalidId_ShouldThrowCustomError() {
        String nonExistentMemberId = "non_existent_id";

        // CustomError가 발생하는지 검증
        CustomError exception = assertThrows(CustomError.class, () -> {
            memberService.findByUserId(nonExistentMemberId);
        });

        // 발생한 CustomError의 메시지를 검증
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    void findById_InvalidBankMember_ShouldThrowException() {
        //given
        String invalidMemberId = null;

        // CustomError가 발생하는지 검증
        CustomError exception = assertThrows(CustomError.class, () -> {
            memberService.findByUserId(invalidMemberId);
        });

        // CustomError가 발생해야 함
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }


    @Test
    void findAll() {
        // Given: 회원 추가
        BankMember saved1 = memberRepository.save(entityConverter.convertToBankMember(bankMemberDto));

        // When: 모든 회원 조회
        List<BankMember> bankMembers = memberRepository.findAll();

        // Then: 조회 결과 검증
        assertThat(bankMembers).hasSize(1); // 예상한 회원 수로 검증
    }

    @Test
    void createBankMember() {
        // Given: BankMemberDto 객체를 생성
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .userId("test")
                .name("John Doe")
                .password("123")
                .roles("USER")
                .email("apple.doe@example.com")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .build();

        // When: createBankMember 메서드를 호출하여 새 BankMember를 생성
        BankMemberDto createdMember = memberService.createBankMember(bankMemberDto, bankMemberDto.getRoles());

        // Then: 생성된 BankMember가 null이 아니고, BankMemberDto의 필드와 일치해야 함
        assertNotNull(createdMember, "The created BankMember should not be null");
        assertEquals(bankMemberDto.getName(), createdMember.getName(), "The names should match");
        assertEquals(bankMemberDto.getEmail(), createdMember.getEmail(), "The emails should match");
    }

    @Test
    void delete() {
        // Given: 기존 회원을 데이터베이스에 저장
        BankMemberDto bankMemberDto = BankMemberDto.builder()
                .userId("test")
                .name("John Doe")
                .password("1234")
                .roles("USER")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .build();

        BankMemberDto rst = memberService.createBankMember(bankMemberDto, bankMemberDto.getRoles());
        String memberId = rst.getUserId();

        // 비밀번호가 틀렸을 때 CustomError가 발생해야 함
        CustomError exception = assertThrows(CustomError.class,
                () -> {
                    memberService.deleteById(memberId, "wrongpassword");
                });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}
