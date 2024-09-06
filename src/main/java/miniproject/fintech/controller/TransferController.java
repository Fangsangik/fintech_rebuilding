package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.TransferServiceImpl;
import miniproject.fintech.type.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferServiceImpl transferService;

    @Autowired
    public TransferController(TransferServiceImpl transferService) {
        this.transferService = transferService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/process")
    @CacheEvict(value = "transfersCache", allEntries = true)
    public ResponseEntity<TransferDto> processTransfer(@RequestBody TransferDto transferDto) {
        // 송금 요청 데이터 유효성 검사
        log.debug("Received processDeposit request: {}", transferDto);
        TransferDto transfer = transferService.processTransfer(transferDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transfer);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Cacheable(value = "transfersCache", key = "#id")
    public ResponseEntity<Transfer> getTransferById(@PathVariable Long id) {
        Transfer transfer = transferService.getTransferById(id);
        return ResponseEntity.ok().body(transfer);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/account/{accountId}")
    @Cacheable(value = "transfersCache", key = "'account_' + #accountId")
    public ResponseEntity<List<TransferDto>> getTransfersByAccountId(@RequestParam String accountNumber) {
        List<TransferDto> transfers = transferService.getTransfersByAccountNumber(accountNumber);
        return ResponseEntity.ok().body(transfers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @CacheEvict(value = "transfersCache", key = "#id")
    public ResponseEntity<String> deleteTransferById(@PathVariable Long id) {
        transferService.deleteTransferById(id);
        return ResponseEntity.ok("Transfer deleted successfully");
    }
}
