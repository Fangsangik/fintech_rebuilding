package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.dto.AdminDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allMembers")
    @Cacheable(value = "adminCache", key = "'allMembers'")
    public ResponseEntity<List<BankMemberDto>> getAllBankMembers(
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        List<BankMemberDto> allMembers = adminService.getAllBankMembers(pageNum, pageSize);
        return ResponseEntity.ok(allMembers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/toggleActivation/{userId}")
    @CacheEvict(value = "adminCache", key = "'allUsers'")
    public ResponseEntity<BankMemberDto> toggleUserActivation(
            @PathVariable String userId,
            @RequestParam boolean isActive) {
        BankMemberDto updatedUser = adminService.toggleUserActivation(userId, isActive);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allTransactions")
    @Cacheable(value = "adminCache", key = "'allTransactions'")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        List<TransactionDto> allTransactions = adminService.getAllTransactions(pageNum, pageSize);
        return ResponseEntity.ok(allTransactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateAdmin/{adminId}")
    @CacheEvict(value = "adminCache", key = "'adminDetails'")
    public ResponseEntity<AdminDto> updateAdmin(
            @PathVariable String adminId,
            @RequestBody AdminDto adminDto) {
        AdminDto updatedAdmin = adminService.updateAdmin(adminId, adminDto);
        return ResponseEntity.ok(updatedAdmin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changeUserPassword")
    @CacheEvict(value = "adminCache", key = "'allUsers'")
    public ResponseEntity<Void> changeUserPassword(
            @RequestParam String adminId,
            @RequestParam String userId,
            @RequestParam String newPassword) {
        adminService.adminChangeUserPassword(adminId, userId, newPassword);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changePassword/{adminId}")
    public ResponseEntity<Void> changeAdminPassword(
            @PathVariable String adminId,
            @RequestParam String newPassword) {
        adminService.changeAdminPassword(adminId, newPassword);
        return ResponseEntity.ok().build();
    }
}
