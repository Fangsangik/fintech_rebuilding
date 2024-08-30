package miniproject.fintech.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
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
}
