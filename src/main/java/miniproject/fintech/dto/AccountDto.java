package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miniproject.fintech.type.AccountStatus;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AccountDto {

    private Long id;

    private String accountNumber;
    private long amount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    private AccountStatus accountStatus;

    private List<DepositDto> deposits;
    private List<TransferDto> sentTransfers;
    private List<TransferDto> receivedTransfers;
    private List<TransactionDto> transactions;
}
