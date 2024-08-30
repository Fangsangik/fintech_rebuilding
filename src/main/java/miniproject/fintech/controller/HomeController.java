package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.service.AccountServiceImpl;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import java.util.Map;


@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    // 홈 페이지 정보 반환
    @GetMapping
    public ResponseEntity<String> home() {
        String homeMessage = "Welcome to the home page!";
        return ResponseEntity.ok(homeMessage);
    }

    // 시스템 상태 확인
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("Account Service", "Running");
        statusMap.put("Member Service", "Running");
        statusMap.put("Deposit Service", "Running");
        statusMap.put("Transaction Service", "Running");
        statusMap.put("Transfer Service", "Running");

        return ResponseEntity.ok(statusMap);
    }

    // 회원 생성 페이지로 리다이렉트 (API 스타일로 응답)
    @GetMapping("/create/member")
    public ResponseEntity<Map<String, String>> redirectToCreateMember() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirect to member creation");
        response.put("redirectUrl", "/member/create");
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    // 계좌 생성 페이지로 리다이렉트 (API 스타일로 응답)
    @GetMapping("/create/account")
    public ResponseEntity<Map<String, String>> redirectToCreateAccount() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirect to account creation");
        response.put("redirectUrl", "/account/create");
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }
}
