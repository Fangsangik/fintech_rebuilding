package miniproject.fintech.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Admin;
import miniproject.fintech.dto.AdminDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<BankMemberDto>> getAllUsers
            (@RequestParam(defaultValue = "0") int pageNum,
             @RequestParam(defaultValue = "5") int pageSize) {
        List<BankMemberDto> allBankMembers = adminService.getAllBankMembers(pageNum, pageSize);
        return ResponseEntity.ok(allBankMembers);
    }

    @PostMapping("/toggle/{id}")
    public ResponseEntity<BankMemberDto> toggleBankMember
            (@PathVariable Long id,
             @RequestParam boolean isActive) {
        BankMemberDto bankMemberDto = adminService.toggleUserActivation(id, isActive);
        return ResponseEntity.ok(bankMemberDto);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<BankMemberDto> update
            (@PathVariable Long id,
             @RequestBody BankMemberDto bankMemberDto) {
        BankMemberDto updated = adminService.updateUserDetails(id, bankMemberDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/getTransaction")
    public ResponseEntity<List<TransactionDto>> getTransaction
            (@RequestParam(defaultValue = "0") int pageNum,
             @RequestParam(defaultValue = "5") int pageSize) {
        List<TransactionDto> allTransactions = adminService.getAllTransactions(pageNum, pageSize);
        return ResponseEntity.ok(allTransactions);
    }

    // 관리자 정보 수정
    @PutMapping("/updateAdmin/{id}")
    public ResponseEntity<AdminDto> updateAdmin
    (@PathVariable Long id,
     @RequestBody AdminDto adminDto) {

        AdminDto updatedAdmin = adminService.updateAdmin(id, adminDto);
        return ResponseEntity.ok(updatedAdmin);
    }

    // 관리자가 사용자 비밀번호 변경
    @PostMapping("/changeUserPassword")
    public ResponseEntity<?> changeUserPassword
    (@RequestParam Long adminId,
     @RequestParam Long userId,
     @RequestParam String newPassword) {
        adminService.adminChangeUserPassword(adminId, userId, newPassword);
        return ResponseEntity.ok().build();
    }

    // 관리자 본인의 비밀번호 변경
    @PostMapping("/changePassword")
    public ResponseEntity<Void> changeAdminPassword
    (@RequestParam Long adminId,
     @RequestParam String newPassword) {
        adminService.changeAdminPassword(adminId, newPassword);
        return ResponseEntity.ok().build();
    }
}

