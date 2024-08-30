package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.service.DepositServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final DepositServiceImpl depositService;

    @PostMapping("/process")
    public ResponseEntity<Deposit> processDeposit
            (@RequestBody DepositDto depositDto) {
        Deposit deposit = depositService.processDeposit(depositDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    @GetMapping("/by-range-date")
    public ResponseEntity<List<Deposit>> findDepositsByDateRange
            (@RequestParam LocalDateTime startDate,
             @RequestParam LocalDateTime endDate,
             @RequestParam int page,
             @RequestBody int size) {
        List<Deposit> deposits = depositService
                .findDepositsByDateRange(startDate, endDate, page, size);

        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<Deposit>> findDepositsByAccountId
            (@PathVariable Long accountId) {
        List<Deposit> deposits = depositService.findDepositsByAccountId(accountId);
        return ResponseEntity.ok(deposits);
    }
}