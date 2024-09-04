package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Admin;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.AdminDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.service.AdminService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allUsers")
    @Cacheable(value = "adminCache", key = "'allUsers'")
    public ResponseEntity<List<BankMember>> getAllUsers(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        List<BankMember> allBankMembers = adminService.getAllBankMembers(pageNum, pageSize);
        return ResponseEntity.ok(allBankMembers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/toggle/{id}")
    @CacheEvict(value = "adminCache", key = "'allUsers'") // 캐시 무효화
    public ResponseEntity<BankMember> toggleBankMember(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        BankMember bankMemberDto = adminService.toggleUserActivation(id, isActive);
        return ResponseEntity.ok(bankMemberDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/{id}")
    @CacheEvict(value = "adminCache", key = "'allUsers'")
    public ResponseEntity<BankMember> update(
            @PathVariable Long id,
            @RequestBody BankMemberDto bankMemberDto) {
        BankMember updated = adminService.updateUserDetails(id, bankMemberDto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getTransaction")
    @Cacheable(value = "adminCache", key = "'transactions'")
    public ResponseEntity<List<Transaction>> getTransaction(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        List<Transaction> allTransactions = adminService.getAllTransactions(pageNum, pageSize);
        return ResponseEntity.ok(allTransactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateAdmin/{id}")
    @CacheEvict(value = "adminCache", key = "'adminDetails'") // 특정 캐시 키 무효화
    public ResponseEntity<Admin> updateAdmin(
            @PathVariable Long id,
            @RequestBody AdminDto adminDto) {
        Admin updatedAdmin = adminService.updateAdmin(id, adminDto);
        return ResponseEntity.ok(updatedAdmin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changeUserPassword")
    @CacheEvict(value = "adminCache", key = "'allUsers'") // 사용자의 비밀번호 변경 시 캐시 무효화
    public ResponseEntity<Void> changeUserPassword(
            @RequestParam Long adminId,
            @RequestParam Long userId,
            @RequestParam String newPassword) {
        adminService.adminChangeUserPassword(adminId, userId, newPassword);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changePassword/{id}")
    public ResponseEntity<Void> changeAdminPassword(
            @PathVariable Long id, // @PathVariable로 변경
            @RequestParam String newPassword) {
        adminService.changeAdminPassword(id, newPassword);
        return ResponseEntity.ok().build();
    }
}
