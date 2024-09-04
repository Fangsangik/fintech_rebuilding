package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.service.DepositServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final DepositServiceImpl depositService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/process")
    @CacheEvict(value = "depositCache", allEntries = true) // 입금 처리 시 캐시 무효화
    public ResponseEntity<Deposit> processDeposit
            (@RequestBody DepositDto depositDto) {
        Deposit deposit = depositService.processDeposit(depositDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-range-date")
    @Cacheable(value = "depositCache", key = "{#startDate, #endDate, #page, #size}")
    public ResponseEntity<List<Deposit>> findDepositsByDateRange
            (@RequestParam LocalDateTime startDate,
             @RequestParam LocalDateTime endDate,
             @RequestParam int page,
             @RequestBody int size) {
        List<Deposit> deposits = depositService
                .findDepositsByDateRange(startDate, endDate, page, size);

        return ResponseEntity.ok(deposits);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-account/{accountId}")
    @CacheEvict(value = "depositCache", allEntries = true) // 입금 후 모든 캐시 무효화
    public ResponseEntity<List<Deposit>> findDepositsByAccountId
            (@PathVariable Long accountId) {
        List<Deposit> deposits = depositService.findDepositsByAccountId(accountId);
        return ResponseEntity.ok(deposits);
    }
}