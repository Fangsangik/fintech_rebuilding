package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.service.DepositServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/deposit")
public class DepositController {

    private final DepositServiceImpl depositService;

    @Autowired
    public DepositController(DepositServiceImpl depositService) {
        this.depositService = depositService;
    }

    //TODO -> 비동기 처리 고려 @Async
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/process")
    @CacheEvict(value = "depositCache", allEntries = true) // 입금 처리 시 캐시 무효화
    public ResponseEntity<DepositDto> processDeposit(@RequestBody DepositDto depositDto) {
        log.debug("Received processDeposit request: {}", depositDto);
        DepositDto deposit = depositService.processDeposit(depositDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-range-date")
    @Cacheable(value = "depositCache", key = "{#startDate, #endDate, #page, #size}")
    public ResponseEntity<List<DepositDto>> findDepositsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam int page,
            @RequestParam int size) {
        List<DepositDto> deposits = depositService.findDepositsByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(deposits);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-account/{accountId}")
    @CacheEvict(value = "depositCache", allEntries = true) // 입금 후 모든 캐시 무효화
    public ResponseEntity<List<DepositDto>> findDepositsByAccountId(@PathVariable Long accountId) {
        List<DepositDto> deposits = depositService.findDepositsByAccountId(accountId);
        return ResponseEntity.ok(deposits);
    }
}
