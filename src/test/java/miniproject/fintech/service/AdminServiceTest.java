package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DtoConverter converter;


    private BankMemberDto bankMemberDto;

    @BeforeEach
    void setUp() {
        BankMember bankMember = memberRepository.save(BankMember.builder()
                .name("test")
                .email("test@test.com")
                .isActive(true)
                .build());

        bankMemberDto = converter.convertToBankMemberDto(bankMember);
    }

    @Test
    void testGetAllBankMembers() {
        int pageNum = 0;
        int pageSize = 10;
        List<BankMemberDto> rst = adminService.getAllBankMembers(pageNum, pageSize);

        assertNotNull(rst);
        assertFalse(rst.isEmpty());
    }

    @Test
    void testToggleUserActivation() {
        BankMember bankMember = memberRepository.findAll().get(0);
        Long bankMemberId = bankMember.getId();
        boolean activeStatus = bankMember.isActive();

        adminService.toggleUserActivation(bankMemberId, !activeStatus);
        BankMember updateBankMember = memberRepository.findById(bankMemberId).orElse(null);

        assertNotNull(updateBankMember);
        assertEquals(!activeStatus, updateBankMember.isActive());
    }

    @Test
    void testUpdateUserDetails() {
        // Arrange
        BankMember bankMember = memberRepository.findAll().get(0);
        Long memberId = bankMember.getId();
        BankMemberDto bankMemberDto = new BankMemberDto();
        bankMemberDto.setName("Updated Name");
        bankMemberDto.setEmail("updated@example.com");

        // Act
        BankMemberDto updatedMemberDto = adminService.updateUserDetails(memberId, bankMemberDto);
        BankMember updatedBankMember = memberRepository.findById(memberId).orElse(null);

        // Assert
        assertNotNull(updatedMemberDto);
        assertNotNull(updatedBankMember);
        assertEquals("Updated Name", updatedBankMember.getName());
        assertEquals("updated@example.com", updatedBankMember.getEmail());
    }
}