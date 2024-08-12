package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.memberservice.MemoryMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static miniproject.fintech.type.ErrorType.*;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemoryMemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<BankMember> createMember(@Valid @RequestBody BankMemberDto bankMemberDto) throws CustomError {
        if (bankMemberDto == null) {
            throw new CustomError(MEMBER_NOT_FOUND);
        }

        BankMember newMember = memberService.createBankMember(bankMemberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankMember> getMemberById(@PathVariable Long id) {
        BankMember findMember = memberService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
        return ResponseEntity.ok(findMember);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<BankMember> updateBankMember(
            @PathVariable Long id,
            @Valid @RequestBody BankMemberDto bankMemberDto) {
        BankMember existingBankMember = memberService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        BankMember updatedMember = memberService.updateMember(existingBankMember, bankMemberDto);
        return ResponseEntity.accepted().body(updatedMember);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBankMember(
            @PathVariable Long id,
            @RequestParam String password) {
        BankMember existingBankMember = memberService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        if (!existingBankMember.getPassword().equals(password)) {
            throw new CustomError(PASSWORD_INCORRECT);
        }

        memberService.deleteById(id, password);
        return ResponseEntity.ok("Member deleted successfully");
    }
}