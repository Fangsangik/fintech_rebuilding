package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.service.DepositServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/deposit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class DepositController {

    private final DepositServiceImpl depositService;

    @PostMapping("/process")
    public ResponseEntity<DepositDto> processDeposit
            (@RequestBody DepositDto depositDto) {
        DepositDto deposit = depositService.processDeposit(depositDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    @GetMapping("/by-range-date")
    public ResponseEntity<List<DepositDto>> findDepositsByDateRange
            (@RequestParam LocalDateTime startDate,
             @RequestParam LocalDateTime endDate,
             @RequestParam int page,
             @RequestBody int size) {
        List<DepositDto> deposits = depositService
                .findDepositsByDateRange(startDate, endDate, page, size);

        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<DepositDto>> findDepositsByAccountId
            (@PathVariable Long accountId) {
        List<DepositDto> deposits = depositService.findDepositsByAccountId(accountId);
        return ResponseEntity.ok(deposits);
    }
}