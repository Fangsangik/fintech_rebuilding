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

    @GetMapping("/")
    public String home() {
        return "Hello World";
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
}
