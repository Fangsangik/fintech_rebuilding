package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.service.TransferServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferServiceImpl transferService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/process")
    @CacheEvict(value = "transfersCache", allEntries = true)
    public ResponseEntity<Transfer> processTransfer(@RequestBody TransferDto transferDto) {
        Transfer transfer = transferService.processTransfer(transferDto);
        return ResponseEntity.ok().body(transfer);
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
    public ResponseEntity<List<TransferDto>> getTransfersByAccountId(@PathVariable Long accountId) {
        List<TransferDto> transfers = transferService.getTransfersByAccountId(accountId);
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
