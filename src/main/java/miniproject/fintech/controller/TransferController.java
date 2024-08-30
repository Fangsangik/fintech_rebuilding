package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.service.TransferServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferServiceImpl transferService;

    @PostMapping("/process")
    public ResponseEntity<Transfer> processTransfer
            (@RequestBody TransferDto transferDto) {
        Transfer transfer = transferService.processTransfer(transferDto);
        return ResponseEntity.ok().body(transfer);
    }
}