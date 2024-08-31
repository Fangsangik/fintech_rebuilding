package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.service.TransferServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class TransferController {

    private final TransferServiceImpl transferService;

    @PostMapping("/process")
    public ResponseEntity<TransferDto> processTransfer
            (@RequestBody TransferDto transferDto) {
        TransferDto transfer = transferService.processTransfer(transferDto);
        return ResponseEntity.ok().body(transfer);
    }
}