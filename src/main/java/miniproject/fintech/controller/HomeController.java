package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.accountservice.AccountService;
import miniproject.fintech.service.memberservice.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static miniproject.fintech.type.ErrorType.MEMBER_NOT_FOUND;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {


    private final AccountService accountService;
    private final MemberService memberService;

    // 홈 페이지 반환
    @GetMapping
    public String home() {
        return "home"; // home.html 템플릿 반환
    }

    // 시스템 상태 확인
    @GetMapping("/status")
    public String status(Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("Account Service", "Running");
        map.put("Member Service", "Running");
        map.put("Deposit Service", "Running");
        map.put("Transaction Service", "Running");
        map.put("Transfer Service", "Running");

        model.addAttribute("statusMap", map);
        return "status"; // status.html 템플릿 반환
    }

    @GetMapping("/create/member")
    public String redirectToCreateMember() {
        return "redirect:/member/create"; // MemberController의 회원 생성 페이지로 리다이렉트
    }

    @GetMapping("/create/account")
    public String redirectToCreateAccount() {
        return "redirect:/account/create";
    }
}
